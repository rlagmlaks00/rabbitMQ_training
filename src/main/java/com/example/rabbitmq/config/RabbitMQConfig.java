package com.example.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  public static final String EXCHANGE_NAME = "order.exchange";
  public static final String QUEUE_NAME = "order.created.queue";
  public static final String ROUTING_KEY = "order.created";

  public static final String DLX_NAME = "order.dlx";
  public static final String DLQ_NAME = "order.dlq";
  public static final String DLQ_ROUTING_KEY = "order.failed";

  // ── 정상 처리 흐름 ──────────────────────────

  // 분류 센터(Exchange)
  @Bean
  public TopicExchange orderExchange() {
    return new TopicExchange(EXCHANGE_NAME);
  }

  // 우편함(Queue) — durable: RabbitMQ 재시작해도 큐가 살아남음
  // + 배달 실패한 메시지를 DLX(order.dlx)로 넘기도록 지정 (DLQ 인자 포함)
  @Bean
  public Queue orderQueue() {
    return QueueBuilder.durable(QUEUE_NAME)
        .withArgument("x-dead-letter-exchange", DLX_NAME)
        .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
        .build();
  }

  // 분류 규칙(Binding) — "order.created"로 시작하는 라우팅 키는 이 큐로
  @Bean
  public Binding orderBinding(Queue orderQueue, TopicExchange orderExchange) {
    return BindingBuilder.bind(orderQueue).to(orderExchange).with(ROUTING_KEY);
  }

  // ── DLQ (실패한 메시지 처리) ────────────────

  // 실패 우편물 전용 분류 센터(DLX, Dead Letter Exchange)
  @Bean
  public DirectExchange dlxExchange() {
    return new DirectExchange(DLX_NAME);
  }

  // 실패 우편물 보관함(DLQ, Dead Letter Queue)
  @Bean
  public Queue dlq() {
    return new Queue(DLQ_NAME, true);
  }

  // 실패 분류 규칙(Binding) — "order.failed" 라우팅 키는 DLQ로
  @Bean
  public Binding dlqBinding(Queue dlq, DirectExchange dlxExchange) {
    return BindingBuilder.bind(dlq).to(dlxExchange).with(DLQ_ROUTING_KEY);
  }

  // ── 메시지 컨버터 ────────────────────────────

  // Jackson 3 기반 JSON 컨버터 + 역직렬화 화이트리스트
  @Bean
  public MessageConverter jsonMessageConverter() {
    return new JacksonJsonMessageConverter("com.example.rabbitdemo.messaging.dto");
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                       MessageConverter jsonMessageConverter) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(jsonMessageConverter);
    return template;
  }
}