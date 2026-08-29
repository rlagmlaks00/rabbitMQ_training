package com.example.rabbitmq.controller;

import com.example.rabbitmq.messaging.OrderProducer;
import com.example.rabbitmq.messaging.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProducer orderProducer;

    @PostMapping
    public String createOrder(@RequestParam String productName, @RequestParam int quantity) {
        String orderId = UUID.randomUUID().toString();
        orderProducer.sendOrderCreatedEvent(new OrderCreatedEvent(orderId, productName, quantity));
        return "주문 접수됨: " + orderId;
    }
}