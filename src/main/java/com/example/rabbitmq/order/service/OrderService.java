package com.example.rabbitmq.order.service;

import com.example.rabbitmq.order.dto.OrderCreatedEvent;
import com.example.rabbitmq.order.producer.BroadcastEventProducer;
import com.example.rabbitmq.order.producer.OrderEventProducer;
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
