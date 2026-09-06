package com.example.rabbitmq.order.adapter.out.messaging;

import com.example.rabbitmq.config.RabbitMQConstants;
import com.example.rabbitmq.order.domain.model.OrderCreatedEvent;
import com.example.rabbitmq.order.domain.port.out.OrderEventBroadcastPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BroadcastEventProducer implements OrderEventBroadcastPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(OrderCreatedEvent event) {
        rabbitTemplate.convertAndSend(
            RabbitMQConstants.FANOUT_EXCHANGE_NAME,
            "",
            event
        );
        System.out.println("팬아웃 주문 이벤트 발행: " + event.orderId());
    }
}
