package com.example.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.example.rabbitmq.config.RabbitMQConstants.DLQ_NAME;
import static com.example.rabbitmq.config.RabbitMQConstants.DLQ_ROUTING_KEY;
import static com.example.rabbitmq.config.RabbitMQConstants.DLX_NAME;

// ── DLQ (실패한 메시지 처리) ────────────────
@Configuration
public class DeadLetterQueueConfig {

  // 실패 우편물 전용 분류 센터(DLX, Dead Letter Exchange)
  @Bean
  public DirectExchange dlxExchange() {
    return new DirectExchange(DLX_NAME);
  }

  // 실패 우편물 보관함(DLQ, Dead Letter Queue) — 마찬가지로 quorum queue로 구성
  @Bean
  public Queue dlq() {
    return QueueBuilder.durable(DLQ_NAME).quorum().build();
  }

  // 실패 분류 규칙(Binding) — "order.failed" 라우팅 키는 DLQ로
  @Bean
  public Binding dlqBinding(Queue dlq, DirectExchange dlxExchange) {
    return BindingBuilder.bind(dlq).to(dlxExchange).with(DLQ_ROUTING_KEY);
  }
}
