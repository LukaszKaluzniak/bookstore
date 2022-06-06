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
            order.setStatus("Rozpoczęte");
        } else {
            order.setStatus("Opłacone");
        }

        List<Order> orders = user.getOrders();
        orders.add(order);

        user.setOrders(orders);

        orderRepository.save(order);

        return "Utworzono zamówienie";
    }

    public String cancelOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if(order != null) {
            User user = userService.getCurrentLoggedInUser();
            if (user.getOrders().contains(order)) {
                switch (order.getStatus()) {
                    case "Rozpoczęte":
                        order.setStatus("Anulowane");
                        orderRepository.save(order);
                        return "Zamówienie zostało anulowane";
                    case "Anulowane":
                        return "Zamówienie zostało już anulowane";
                    case "Opłacone":
                        return "Nie można anulować opłaconego zamówienia";
                    case "Zrealizowane":
                        return "Nie można anulować zrealizowanego zamówienia";
                }
            }
        }
        return "Nie udało się anulować wskazanego zamówienia";
    }

    public String cancelOrderAdmin(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if(order != null) {
            switch (order.getStatus()) {
                case "Rozpoczęte":
                    order.setStatus("Anulowane");
                    orderRepository.save(order);
                    return "Zamówienie zostało anulowane";
                case "Anulowane":
                    return "Zamówienie zostało już anulowane";
                case "Opłacone":
                    return "Nie można anulować opłaconego zamówienia";
                case "Zrealizowane":
                    return "Nie można anulować zrealizowanego zamówienia";
            }
        }
        return "Takie zamówienie nie istnieje";
    }

    public String acceptOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            switch (order.getStatus()) {
                case "Opłacone":
                    order.setStatus("Zrealizowane");
                    orderRepository.save(order);
                    return "Zamówienie zostało zrealizowane";
                case "Zrealizowane":
                    return "Zamówienie zostało już zrealizowane";
                case "Anulowane":
                    return "Nie można zrealizować anulowanego zamówienia";
                case "Rozpoczęte":
                    return "Można zrealizować jedynie opłacone zamówienia";
            }
        }
        return "Takie zamówienie nie istnieje";
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
        order.setStatus("Opłacone");
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
