package com.example.rabbitmq.controller;

import com.example.rabbitmq.messaging.dto.OrderCreatedEvent;
import com.example.rabbitmq.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public String createOrder(@RequestParam String productName, @RequestParam int quantity) {
        String orderId = UUID.randomUUID().toString();
        orderService.publish(new OrderCreatedEvent(orderId, productName, quantity));
        return "주문 접수됨: " + orderId;
    }

    @PostMapping("/fanout")
    public String createFanoutOrder(@RequestParam String productName, @RequestParam int quantity) {
        String orderId = UUID.randomUUID().toString();
        orderService.fanoutPublish(new OrderCreatedEvent(orderId, productName, quantity));
        return "팬아웃 주문 접수됨: " + orderId;
    }
}