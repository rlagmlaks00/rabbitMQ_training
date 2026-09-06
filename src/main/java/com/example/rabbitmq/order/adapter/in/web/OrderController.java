package com.example.rabbitmq.order.adapter.in.web;

import com.example.rabbitmq.order.domain.model.OrderCreatedEvent;
import com.example.rabbitmq.order.domain.port.in.OrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderUseCase orderUseCase;

    @PostMapping
    public String createOrder(@RequestParam String productName, @RequestParam int quantity) {
        String orderId = UUID.randomUUID().toString();
        orderUseCase.publish(new OrderCreatedEvent(orderId, productName, quantity));
        return "주문 접수됨: " + orderId;
    }

    @PostMapping("/fanout")
    public String createFanoutOrder(@RequestParam String productName, @RequestParam int quantity) {
        String orderId = UUID.randomUUID().toString();
        orderUseCase.fanoutPublish(new OrderCreatedEvent(orderId, productName, quantity));
        return "팬아웃 주문 접수됨: " + orderId;
    }
}
