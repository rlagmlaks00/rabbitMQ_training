package com.example.rabbitmq.service;

import com.example.rabbitmq.messaging.BroadcastEventProducer;
import com.example.rabbitmq.messaging.OrderEventProducer;
import com.example.rabbitmq.messaging.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderEventProducer orderEventProducer;
  private final BroadcastEventProducer broadcastEventProducer;

  public void publish(OrderCreatedEvent event) {
    int publish_cnt = event.quantity();

    for(int i = 0; i < publish_cnt; i++) {
      orderEventProducer.sendOrderCreatedEvent(event);
    }
  }

  public void fanoutPublish(OrderCreatedEvent event) {
    int publish_cnt = event.quantity();

    for(int i = 0; i < publish_cnt; i++) {
      broadcastEventProducer.broadcastOrderCreatedEvent(event);
    }
  }
}
