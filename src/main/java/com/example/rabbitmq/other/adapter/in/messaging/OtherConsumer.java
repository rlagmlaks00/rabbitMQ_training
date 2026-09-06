package com.example.rabbitmq.other.adapter.in.messaging;

import com.example.rabbitmq.config.RabbitMQConstants;
import com.example.rabbitmq.order.domain.model.OrderCreatedEvent;
import com.example.rabbitmq.other.domain.port.in.HandleOtherCreatedUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OtherConsumer {

    private final HandleOtherCreatedUseCase handleOtherCreatedUseCase;

    @RabbitListener(queues = RabbitMQConstants.OTHER_QUEUE_NAME)
    public void handleOtherCreated(OrderCreatedEvent event) throws InterruptedException {
        handleOtherCreatedUseCase.handle(event);
    }
}
