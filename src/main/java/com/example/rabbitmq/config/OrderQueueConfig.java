package com.example.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.example.rabbitmq.config.RabbitMQConstants.DLQ_ROUTING_KEY;
import static com.example.rabbitmq.config.RabbitMQConstants.DLX_NAME;
import static com.example.rabbitmq.config.RabbitMQConstants.EXCHANGE_NAME;
import static com.example.rabbitmq.config.RabbitMQConstants.FANOUT_EXCHANGE_NAME;
import static com.example.rabbitmq.config.RabbitMQConstants.QUEUE_NAME;
import static com.example.rabbitmq.config.RabbitMQConstants.ROUTING_KEY;

// ── 정상 처리 흐름 ──────────────────────────
@Configuration
public class OrderQueueConfig {

  // 분류 센터(Exchange)
  @Bean
  public TopicExchange orderExchange() {
    return new TopicExchange(EXCHANGE_NAME);
  }

  // 우편함(Queue) — quorum: 클러스터 노드 간 Raft 복제로 노드 장애에도 큐 생존
  // + 배달 실패한 메시지를 DLX(order.dlx)로 넘기도록 지정 (DLQ 인자 포함)
  @Bean
  public Queue orderQueue() {
    return QueueBuilder.durable(QUEUE_NAME)
        .quorum()
        .withArgument("x-dead-letter-exchange", DLX_NAME)
        .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
        .build();
  }

  // 분류 규칙(Binding) — "order.created"로 시작하는 라우팅 키는 이 큐로
  @Bean
  public Binding orderBinding(Queue orderQueue, TopicExchange orderExchange) {
    return BindingBuilder.bind(orderQueue).to(orderExchange).with(ROUTING_KEY);
  }

  // broadcast 분류 센터(Fanout Exchange) — 라우팅 키 없이 바인딩된 모든 큐로 전달
  @Bean
  public FanoutExchange orderFanoutExchange() {
    return new FanoutExchange(FANOUT_EXCHANGE_NAME);
  }

  // 분류 규칙(Binding) — fanout이므로 라우팅 키 무시, 바인딩만 되어 있으면 전달됨
  @Bean
  public Binding orderFanoutBinding(Queue orderQueue, FanoutExchange orderFanoutExchange) {
    return BindingBuilder.bind(orderQueue).to(orderFanoutExchange);
  }
}
