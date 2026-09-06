package com.example.rabbitmq.config;

import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static com.example.rabbitmq.config.RabbitMQConstants.DLQ_NAME;
import static com.example.rabbitmq.config.RabbitMQConstants.DLQ_ROUTING_KEY;
import static com.example.rabbitmq.config.RabbitMQConstants.DLX_NAME;
import static com.example.rabbitmq.config.RabbitTopologySupport.binding;
import static com.example.rabbitmq.config.RabbitTopologySupport.plainQueue;

// ── DLQ 도메인: order.dlx(direct) + order.dlq ──
// DLQ 큐 자체는 더 이상 데드레터링되지 않으므로 plainQueue를 사용한다.
@Configuration
public class DeadLetterRabbitConfig {

  @Bean
  public Declarables deadLetterRabbitTopology() {
    List<Declarable> declarables = List.of(
        new DirectExchange(DLX_NAME),
        plainQueue(DLQ_NAME),
        binding(DLX_NAME, DLQ_NAME, DLQ_ROUTING_KEY)
    );
    return new Declarables(declarables);
  }
}
