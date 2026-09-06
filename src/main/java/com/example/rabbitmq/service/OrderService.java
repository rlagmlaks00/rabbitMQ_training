package com.example.rabbitmq.service;

import com.example.rabbitmq.messaging.OrderProducer;
import com.example.rabbitmq.messaging.dto.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderProducer orderProducer;

  public void publish(OrderCreatedEvent event) {
    int publish_cnt = event.quantity();

    for(int i = 0; i < publish_cnt; i++) {
      orderProducer.sendOrderCreatedEvent(event);
    }
  }

  public void fanoutPublish(OrderCreatedEvent event) {
    int publish_cnt = event.quantity();

    for(int i = 0; i < publish_cnt; i++) {
      orderProducer.fanoutOrderCreatedEvent(event);
    }
  }
}
