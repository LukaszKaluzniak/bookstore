package com.example.bookstore.services;

import com.example.bookstore.models.Book;
import com.example.bookstore.models.Book2;
import com.example.bookstore.models.Order;
import com.example.bookstore.models.User;
import com.example.bookstore.repositories.OrderRepository;
import com.example.bookstore.repositories.UserRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    public Optional<Order> findById(Integer orderId) {
        User user = userService.getCurrentLoggedInUser();
        Order order = orderRepository.findById(orderId).orElse(null);

        if (user.getOrders().contains(order)) {
            return Optional.ofNullable(order);
        }
        return Optional.empty();
    }

    public List<Order> getOrders() {

        User user = userService.getCurrentLoggedInUser();
        return user.getOrders();

    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public String createANewOrder() {
        User user = userService.getCurrentLoggedInUser();

        Order order = new Order();

        Date d = new Date();
        Date date = new Date(d.getTime());

        order.setDate(date);

        Set<Book2> books2 = new HashSet<>();
        Double price = 0.0;
        for (Book2 book2 : user.getBooks2()) {
            books2.add(book2);
            price += book2.getPrice();
        }
        order.setBooks2(books2);
        order.setPrice(price);
        order.setPaymentId(randomizePaymentId());

        if (price != 0.0) {
            order.setStatus("Rozpocz??te");
        } else {
            order.setStatus("Op??acone");
        }

        List<Order> orders = user.getOrders();
        orders.add(order);

        user.setOrders(orders);

        orderRepository.save(order);

        return "Utworzono zam??wienie";
    }

    public String cancelOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if(order != null) {
            User user = userService.getCurrentLoggedInUser();
            if (user.getOrders().contains(order)) {
                switch (order.getStatus()) {
                    case "Rozpocz??te":
                        order.setStatus("Anulowane");
                        orderRepository.save(order);
                        return "Zam??wienie zosta??o anulowane";
                    case "Anulowane":
                        return "Zam??wienie zosta??o ju?? anulowane";
                    case "Op??acone":
                        return "Nie mo??na anulowa?? op??aconego zam??wienia";
                    case "Zrealizowane":
                        return "Nie mo??na anulowa?? zrealizowanego zam??wienia";
                }
            }
        }
        return "Nie uda??o si?? anulowa?? wskazanego zam??wienia";
    }

    public String cancelOrderAdmin(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if(order != null) {
            switch (order.getStatus()) {
                case "Rozpocz??te":
                    order.setStatus("Anulowane");
                    orderRepository.save(order);
                    return "Zam??wienie zosta??o anulowane";
                case "Anulowane":
                    return "Zam??wienie zosta??o ju?? anulowane";
                case "Op??acone":
                    return "Nie mo??na anulowa?? op??aconego zam??wienia";
                case "Zrealizowane":
                    return "Nie mo??na anulowa?? zrealizowanego zam??wienia";
            }
        }
        return "Takie zam??wienie nie istnieje";
    }

    public String acceptOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            switch (order.getStatus()) {
                case "Op??acone":
                    order.setStatus("Zrealizowane");
                    orderRepository.save(order);
                    return "Zam??wienie zosta??o zrealizowane";
                case "Zrealizowane":
                    return "Zam??wienie zosta??o ju?? zrealizowane";
                case "Anulowane":
                    return "Nie mo??na zrealizowa?? anulowanego zam??wienia";
                case "Rozpocz??te":
                    return "Mo??na zrealizowa?? jedynie op??acone zam??wienia";
            }
        }
        return "Takie zam??wienie nie istnieje";
    }

    public Order getById(Integer orderId) {
        User user = userService.getCurrentLoggedInUser();
        List<Order> orders = user.getOrders();
        Order order = null;

        for (Order o : orders) {
            if (o.getId().equals(orderId)) {
                order = o;
                break;
            }
        }

        return order;
    }

    public void pay(Order order) {
        order.setStatus("Op??acone");
        order.setPaymentId(randomizePaymentId());
        orderRepository.save(order);
    }

    public String randomizePaymentId() {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(128);
        for (int i = 0; i < 128; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

}
