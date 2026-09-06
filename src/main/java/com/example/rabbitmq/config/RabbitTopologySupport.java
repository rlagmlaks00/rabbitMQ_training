package com.example.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;

import static com.example.rabbitmq.config.RabbitMQConstants.DLQ_ROUTING_KEY;
import static com.example.rabbitmq.config.RabbitMQConstants.DLX_NAME;

// ── 도메인별 Rabbit*Config가 공통으로 쓰는 선언 헬퍼 ──
// 큐 생성 규칙(quorum + DLX 인자)과 바인딩 생성 방식을 한곳에 모아
// 각 도메인 Config가 자신의 exchange/queue/binding만 선언하면 되도록 한다.
public final class RabbitTopologySupport {

  private RabbitTopologySupport() {
  }

  // 실패 시 공용 DLX(order.dlx)로 데드레터링되는 quorum queue
  public static Queue deadLetteredQueue(String name) {
    return QueueBuilder.durable(name)
        .quorum()
        .withArgument("x-dead-letter-exchange", DLX_NAME)
        .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
        .build();
  }

  // DLQ로 데드레터링되지 않는 일반 quorum queue
  public static Queue plainQueue(String name) {
    return QueueBuilder.durable(name).quorum().build();
  }

  // 이름만으로 바인딩 선언 — Exchange/Queue 빈을 직접 주입받을 필요가 없어
  // 파라미터명-빈이름 불일치로 인한 우발적 매칭 문제가 생기지 않는다.
  public static Binding binding(String exchange, String queue, String routingKey) {
    return new Binding(queue, Binding.DestinationType.QUEUE, exchange, routingKey, null);
  }
}
