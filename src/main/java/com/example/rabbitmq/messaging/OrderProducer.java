package com.example.rabbitmq.messaging;

import com.example.rabbitmq.config.RabbitMQConstants;
import com.example.rabbitmq.messaging.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.EXCHANGE_NAME,
                RabbitMQConstants.ROUTING_KEY,
                event
        );
        System.out.println("주문 이벤트 발행: " + event.orderId());
    }
}