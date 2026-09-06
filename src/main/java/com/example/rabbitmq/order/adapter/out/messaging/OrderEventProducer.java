package com.example.rabbitmq.order.adapter.out.messaging;

import com.example.rabbitmq.config.RabbitMQConstants;
import com.example.rabbitmq.order.domain.model.OrderCreatedEvent;
import com.example.rabbitmq.order.domain.port.out.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventProducer implements OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(OrderCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.EXCHANGE_NAME,
                RabbitMQConstants.ROUTING_KEY,
                event
        );
        System.out.println("주문 이벤트 발행: " + event.orderId());
    }
}
