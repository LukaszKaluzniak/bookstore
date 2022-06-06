package com.example.bookstore.controllers;

import com.example.bookstore.models.Book2;
import com.example.bookstore.models.Order;
import com.example.bookstore.repositories.UserRepository;
import com.example.bookstore.services.BookService;
import com.example.bookstore.services.OrderService;
import com.example.bookstore.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookService bookService;

    @GetMapping("/orders")
    public String getOrders(Model model) {

        List<Order> orders = orderService.getOrders();
        model.addAttribute("orders", orders);

        return "order";
    }

    @RequestMapping(value="/orders/createANewOrder", method={RequestMethod.PUT, RequestMethod.GET})
    public RedirectView createANewOrder(RedirectAttributes redirectAttributes) {
        String message = "Koszyk jest pusty";
        if (!userService.getCurrentLoggedInUser().getBooks2().isEmpty()) {
            message = orderService.createANewOrder();
            bookService.removeAllFromCart();
        }

        RedirectView redirectView = new RedirectView("/orders", true);
        redirectAttributes.addFlashAttribute("message", message);
        return redirectView;
    }


    @RequestMapping("/orders/cancel/{orderId}")
    public RedirectView cancelOrder(@PathVariable Integer orderId, RedirectAttributes redirectAttributes) {
        String message = orderService.cancelOrder(orderId);
        RedirectView redirectView = new RedirectView("/orders", true);
        redirectAttributes.addFlashAttribute("message", message);
        return redirectView;
    }

    @RequestMapping("admin/orders/cancel/{orderId}")
    public RedirectView cancelOrderAdmin(@PathVariable Integer orderId, RedirectAttributes redirectAttributes) {
        String message = orderService.cancelOrderAdmin(orderId);
        RedirectView redirectView = new RedirectView("/admin/orders", true);
        redirectAttributes.addFlashAttribute("message", message);
        return redirectView;
    }

    @RequestMapping("admin/orders/accept/{orderId}")
    public RedirectView acceptOrderAdmin(@PathVariable Integer orderId, RedirectAttributes redirectAttributes) {
        String message = orderService.acceptOrder(orderId);
        RedirectView redirectView = new RedirectView("/admin/orders", true);
        redirectAttributes.addFlashAttribute("message", message);
        return redirectView;
    }

    @RequestMapping("orders/details/{orderId}")
    public String showDetailsOrder(@PathVariable Integer orderId, Model model) {
        Order order = orderService.getById(orderId);
        model.addAttribute("order", order);

        Set<Book2> orderBooks2 = null;
        if (order != null) {
            orderBooks2 = order.getBooks2();
        }

        List<String> coverList = new ArrayList<>();
        if (orderBooks2 != null) {
            for (Book2 book2 : orderBooks2) {
                if (book2.getCover() != null) {
                    coverList.add(Base64.getEncoder().encodeToString(book2.getCover()));
                } else {
                    coverList.add("");
                }
            }
        }

        model.addAttribute("orderBooks", orderBooks2);
        model.addAttribute("covers", coverList);

        return "order-details";
    }

    @GetMapping("/admin/orders")
    public String getOrdersAdmin(Model model) {

        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);

        return "admin/order";
    }

    @RequestMapping("admin/orders/details/{orderId}")
    public String showDetailsOrderAdmin(@PathVariable Integer orderId, Model model) {
        Order order = orderService.findById(orderId).orElse(null);
        model.addAttribute("order", order);

        Set<Book2> orderBooks2 = null;
        if (order != null) {
            orderBooks2 = order.getBooks2();
        }

        List<String> coverList = new ArrayList<>();
        if (orderBooks2 != null) {
            for (Book2 book2 : orderBooks2) {
                if (book2.getCover() != null) {
                    coverList.add(Base64.getEncoder().encodeToString(book2.getCover()));
                } else {
                    coverList.add("");
                }
            }
        }

        model.addAttribute("orderBooks", orderBooks2);
        model.addAttribute("covers", coverList);

        return "admin/order-details";
    }

}
