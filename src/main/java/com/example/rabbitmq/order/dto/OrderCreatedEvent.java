package com.example.rabbitmq.order.dto;

public record OrderCreatedEvent(String orderId, String productName, int quantity) {}
