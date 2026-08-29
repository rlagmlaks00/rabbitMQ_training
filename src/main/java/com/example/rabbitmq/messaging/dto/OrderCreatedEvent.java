package com.example.rabbitmq.messaging.dto;

public record OrderCreatedEvent(String orderId, String productName, int quantity) {}