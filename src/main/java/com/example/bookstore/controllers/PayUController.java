package com.example.bookstore.controllers;

import com.example.bookstore.models.*;
import com.example.bookstore.services.OrderService;
import com.example.bookstore.services.PayUOrderService;
import com.example.bookstore.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Optional;

import static com.example.bookstore.models.OrderResponse.Status.STATUS_CODE_SUCCESS;

@Controller
@RequiredArgsConstructor
public class PayUController {

    @Autowired
    private PayUOrderService payUOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/orders/payment/{orderId}")
    public String getPaymentView(@PathVariable Integer orderId, Model model) {
        User user = userService.getCurrentLoggedInUser();
        Order order = orderService.findById(orderId).orElse(null);

        if (user.getOrders().contains(order) && order != null) {
            model.addAttribute("order", order);
            return "payment";
        }

        return "redirect:/orders";
    }

    @PostMapping("/orders/payment/{orderId}")
    public RedirectView handleCheckout(@RequestParam("email") String email, @PathVariable Integer orderId,
                                       HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {

        Order order = orderService.findById(orderId).orElse(null);

        String message = "";
        RedirectView redirectView = new RedirectView();
        redirectView.setContextRelative(true);
        boolean orderCannotBeFinished = false;

        if (order == null) {
            message = "Takie zamówienie nie istnieje";
            orderCannotBeFinished = true;
        } else {
            switch (order.getStatus()) {
                case "Anulowane":
                    message = "Nie można opłacić anulowanego zamówienia";
                    orderCannotBeFinished = true;
                    break;
                case "Opłacone":
                    message = "Zamówienie zostało już opłacone";
                    orderCannotBeFinished = true;
                    break;
                case "Zrealizowane":
                    message = "Zamówienie zostało już zrealizowane";
                    orderCannotBeFinished = true;
                    break;
            }
        }

        if (orderCannotBeFinished) {
            redirectView.setUrl("/orders");
            redirectAttributes.addFlashAttribute("message", message);
            return redirectView;
        }

        OrderRequest orderRequest = prepareOrderRequest(order, email, request);
        OrderResponse orderResponse = payUOrderService.order(orderRequest, order);

        if (!orderResponse.getStatus().getStatusCode().equals(STATUS_CODE_SUCCESS)) {
            redirectView.setUrl("/orders");
            redirectAttributes.addFlashAttribute("message", "Wystąpił problem z realizacją płatności");
            return redirectView;
        }

        return new RedirectView(orderResponse.getRedirectUri());
    }

    @GetMapping("/orders/payment/callback/{orderId}/{paymentId}")
    public RedirectView handlePaymentCallback(@PathVariable Integer orderId, @PathVariable String paymentId, @RequestParam Optional<String> error, RedirectAttributes redirectAttributes) {
        User user = userService.getCurrentLoggedInUser();
        Order order = orderService.findById(orderId).orElse(null);
        String message = "";

        if (user.getOrders().contains(order)) {
            if (order == null || error.isPresent()) {
                message = "Płatność nie została zrealizowana";
                if (order != null) {
                    order.setPaymentId(orderService.randomizePaymentId());
                }
                RedirectView redirectView = new RedirectView("/orders", true);
                redirectAttributes.addFlashAttribute("message", message);
                return redirectView;
            }

            if (paymentId.equals(order.getPaymentId())) {
                orderService.pay(order);
                message = "Płatność została zrealizowana";
            }
        }

        RedirectView redirectView = new RedirectView("/orders", true);
        redirectAttributes.addFlashAttribute("message", message);
        return redirectView;
    }

    private OrderRequest prepareOrderRequest(Order order, String email, HttpServletRequest request) {
        User user = userService.getCurrentLoggedInUser();

        return OrderRequest.builder()
                .customerIp(request.getRemoteAddr())
                .merchantPosId("145227")
                .description("Księgarnia")
                .currencyCode("PLN")
                .totalAmount(String.valueOf(Math.round(order.getPrice() * 100)))
                .products(
                        Collections.singletonList(
                                Product.builder()
                                        .name("Książka")
                                        .quantity("1")
                                        .unitPrice("0")
                                        .build()
                        ))
                .buyer(
                        Buyer.builder()
                                .email(email)
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .language("pl")
                                .build()
                ).build();
    }
}
