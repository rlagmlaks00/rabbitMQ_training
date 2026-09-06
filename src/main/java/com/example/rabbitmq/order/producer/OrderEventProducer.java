package com.example.rabbitmq.order.producer;

import com.example.rabbitmq.config.RabbitMQConstants;
import com.example.rabbitmq.order.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

// order.exchange(topic)로만 발행 — order.created.queue(주문 도메인) 한 곳만 수신한다.
@Service
@RequiredArgsConstructor
public class OrderEventProducer {

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
