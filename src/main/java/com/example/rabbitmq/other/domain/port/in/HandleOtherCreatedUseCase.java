package com.example.rabbitmq.other.domain.port.in;

import com.example.rabbitmq.order.domain.model.OrderCreatedEvent;

// ── 인바운드 포트 ──
// 메시징 in 어댑터(OtherConsumer)로부터 수신된 브로드캐스트 이벤트를 처리한다.
public interface HandleOtherCreatedUseCase {

  void handle(OrderCreatedEvent event) throws InterruptedException;
}
