package com.example.bookstore.services;

import com.example.bookstore.models.Book2;
import com.example.bookstore.models.Order;
import com.example.bookstore.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SoldService {

    @Autowired
    private UserService userService;

    public Set<Book2> getSoldBooks() {
        User user = userService.getCurrentLoggedInUser();
        List<Order> orders = user.getOrders();

        Set<Book2> soldBooks = new HashSet<>();
        for (Order order : orders) {
            if (order.getStatus() != null && order.getStatus().equals("Zrealizowane")) {
                Set<Book2> books2 = order.getBooks2();
                soldBooks.addAll(books2);
            }
        }

        return soldBooks;
    }
}
