package com.example.rabbitmq.order.domain.port.in;

import com.example.rabbitmq.order.domain.model.OrderCreatedEvent;

// ── 인바운드 포트 ──
// 메시징 in 어댑터(OrderConsumer)로부터 수신된 주문 생성 이벤트를 처리한다.
public interface HandleOrderCreatedUseCase {

  void handle(OrderCreatedEvent event) throws InterruptedException;
}
