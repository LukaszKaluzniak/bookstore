package com.example.bookstore.controllers;

import com.example.bookstore.models.Book2;
import com.example.bookstore.models.User;
import com.example.bookstore.services.BookService;
import com.example.bookstore.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;

@Controller
public class CartController {
    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @RequestMapping("/cart")
    public String showCart(Model model) {
        User user = userService.getCurrentLoggedInUser();

        Set<Book2> bookSet2 = user.getBooks2();

        List<String> coverList = new ArrayList<>();

        for (Book2 book2 : bookSet2) {
            if (book2.getCover() != null) {
                coverList.add(Base64.getEncoder().encodeToString(book2.getCover()));
            } else {
                coverList.add("");
            }
        }

        model.addAttribute("books2", bookSet2);
        model.addAttribute("covers", coverList);

        Double totalPrice = 0.0;
        for (Book2 book2 : bookSet2) {
            totalPrice += book2.getPrice();
        }
        model.addAttribute("total_price", totalPrice);

        return "cart";
    }

    @RequestMapping("/cart/removeAll")
    public RedirectView removeAll(RedirectAttributes redirectAttributes) {
        String message = bookService.removeAllFromCart();

        RedirectView redirectView = new RedirectView("/cart", true);
        redirectAttributes.addFlashAttribute("message", message);
        return redirectView;
    }
}
