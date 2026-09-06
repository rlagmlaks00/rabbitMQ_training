package com.example.rabbitmq.messaging;

import com.example.rabbitmq.config.RabbitMQConstants;
import com.example.rabbitmq.messaging.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

// order.fanout.exchange(fanout)로 발행 — 라우팅 키 없이 바인딩된 모든 큐(order.created.queue,
// other.new.queue 등 여러 도메인)로 전달되는 브로드캐스트 발행 전용 Producer.
@Service
@RequiredArgsConstructor
public class BroadcastEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void broadcastOrderCreatedEvent(OrderCreatedEvent event) {
        rabbitTemplate.convertAndSend(
            RabbitMQConstants.FANOUT_EXCHANGE_NAME,
            "",
            event
        );
        System.out.println("팬아웃 주문 이벤트 발행: " + event.orderId());
    }
}
