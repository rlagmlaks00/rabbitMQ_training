package com.example.rabbitmq.order.application;

import com.example.rabbitmq.order.domain.model.OrderCreatedEvent;
import com.example.rabbitmq.order.domain.port.in.OrderUseCase;
import com.example.rabbitmq.order.domain.port.out.OrderEventBroadcastPublisher;
import com.example.rabbitmq.order.domain.port.out.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderUseCase {

  private final OrderEventPublisher orderEventPublisher;
  private final OrderEventBroadcastPublisher orderEventBroadcastPublisher;

  @Override
  public void publish(OrderCreatedEvent event) {
    int publish_cnt = event.quantity();

    for(int i = 0; i < publish_cnt; i++) {
      orderEventPublisher.publish(event);
    }
  }

  @Override
  public void fanoutPublish(OrderCreatedEvent event) {
    int publish_cnt = event.quantity();

    for(int i = 0; i < publish_cnt; i++) {
      orderEventBroadcastPublisher.publish(event);
    }
  }
}
