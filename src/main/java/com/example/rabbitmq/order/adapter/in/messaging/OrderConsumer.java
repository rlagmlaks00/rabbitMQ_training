package com.example.rabbitmq.order.adapter.in.messaging;

import com.example.rabbitmq.config.RabbitMQConstants;
import com.example.rabbitmq.order.domain.model.OrderCreatedEvent;
import com.example.rabbitmq.order.domain.port.in.HandleOrderCreatedUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderConsumer {

    private final HandleOrderCreatedUseCase handleOrderCreatedUseCase;

    @RabbitListener(queues = RabbitMQConstants.QUEUE_NAME)
    public void handleOrderCreated(OrderCreatedEvent event) throws InterruptedException {
        handleOrderCreatedUseCase.handle(event);
    }
}
