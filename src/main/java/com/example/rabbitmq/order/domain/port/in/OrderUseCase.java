package com.example.rabbitmq.order.domain.port.in;

import com.example.rabbitmq.order.domain.model.OrderCreatedEvent;

// ── 인바운드 포트 ──
// 외부(web 등 in 어댑터)가 주문 도메인에 접근하는 유일한 통로.
public interface OrderUseCase {

  void publish(OrderCreatedEvent event);

  void fanoutPublish(OrderCreatedEvent event);
}
