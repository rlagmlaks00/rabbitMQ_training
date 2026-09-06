package com.example.rabbitmq.order.domain.port.out;

import com.example.rabbitmq.order.domain.model.OrderCreatedEvent;

// ── 아웃바운드 포트 ──
// order.fanout.exchange(fanout)로 발행 — 라우팅 키 없이 바인딩된 모든 큐(order.created.queue,
// other.new.queue 등 여러 도메인)로 전달되는 브로드캐스트 발행 전용 포트.
public interface OrderEventBroadcastPublisher {

  void publish(OrderCreatedEvent event);
}
