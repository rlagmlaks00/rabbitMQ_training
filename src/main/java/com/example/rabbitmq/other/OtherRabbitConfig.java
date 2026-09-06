package com.example.rabbitmq.other;

import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static com.example.rabbitmq.config.RabbitMQConstants.FANOUT_EXCHANGE_NAME;
import static com.example.rabbitmq.config.RabbitMQConstants.OTHER_QUEUE_NAME;
import static com.example.rabbitmq.config.RabbitTopologySupport.binding;
import static com.example.rabbitmq.config.RabbitTopologySupport.plainQueue;

// ── Other 도메인: other.new.queue ──
// order.fanout.exchange는 order 패키지(OrderRabbitConfig)가 소유/선언하며, 여기서는 이름으로만 바인딩한다.
// otherQueue는 실패해도 DLQ로 보내지 않음(plainQueue).
@Configuration
public class OtherRabbitConfig {

  @Bean
  public Declarables otherRabbitTopology() {
    List<Declarable> declarables = List.of(
        plainQueue(OTHER_QUEUE_NAME),
        binding(FANOUT_EXCHANGE_NAME, OTHER_QUEUE_NAME, "") // fanout: 키 무시, 브로드캐스트 수신
    );
    return new Declarables(declarables);
  }
}
