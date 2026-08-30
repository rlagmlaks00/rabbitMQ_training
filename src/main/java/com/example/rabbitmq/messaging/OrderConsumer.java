package com.example.rabbitmq.messaging;

import com.example.rabbitmq.config.RabbitMQConfig;
import com.example.rabbitmq.messaging.dto.OrderCreatedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleOrderCreated(OrderCreatedEvent event) {
        System.out.println("주문 처리 시작: " + event.orderId());
        throw new RuntimeException("일부러 실패시켜서 DLQ 테스트");
        // 실제 비즈니스 로직 (재고 차감, 알림 발송 등)
    }
}