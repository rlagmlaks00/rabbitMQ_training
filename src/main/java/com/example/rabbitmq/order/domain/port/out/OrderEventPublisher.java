package com.example.rabbitmq.order.domain.port.out;

import com.example.rabbitmq.order.domain.model.OrderCreatedEvent;

// ── 아웃바운드 포트 ──
// order.exchange(topic)로만 발행 — order.created.queue(주문 도메인) 한 곳만 수신한다.
public interface OrderEventPublisher {

  void publish(OrderCreatedEvent event);
}
