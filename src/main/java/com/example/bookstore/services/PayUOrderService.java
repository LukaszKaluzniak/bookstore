package com.example.bookstore.services;

import javax.annotation.Resource;

import com.example.bookstore.models.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.bookstore.models.OrderRequest;
import com.example.bookstore.models.OrderResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;


@Service
@RequiredArgsConstructor
public class PayUOrderService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Resource(name = "payuApiRestTemplate")
    private RestTemplate restTemplate;

    final private String serverAddress = "http://localhost:8080";

    @SneakyThrows
    public OrderResponse order(final OrderRequest orderRequest, Order order) {
        if (orderRequest != null) {
            orderRequest.setMerchantPosId("145227");
            orderRequest.setDescription("Księgarnia");

            orderRequest.setContinueUrl("http://localhost:8080/orders/payment/callback/" + order.getId() + "/" + order.getPaymentId());
        }

        ResponseEntity<String> jsonResponse = restTemplate.postForEntity("https://secure.payu.com/api/v2_1/orders", orderRequest, String.class);

        return objectMapper.readValue(jsonResponse.getBody(), OrderResponse.class);
    }
}
