package com.example.rabbitmq.order.domain.model;

public record OrderCreatedEvent(String orderId, String productName, int quantity) {}
