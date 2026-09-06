package com.example.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.example.rabbitmq.config.RabbitMQConstants.*;
import static com.example.rabbitmq.config.RabbitMQConstants.DLQ_ROUTING_KEY;

@Configuration
public class OtherQueueConfig {

  // 우편함(Queue) — quorum: 클러스터 노드 간 Raft 복제로 노드 장애에도 큐 생존
  // + 배달 실패한 메시지를 DLX(order.dlx)로 넘기도록 지정 (DLQ 인자 포함)
  @Bean
  public Queue otherQueue() {
    return QueueBuilder.durable(OTHER_QUEUE_NAME)
        .quorum()
        .withArgument("x-dead-letter-exchange", DLX_NAME)
        .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
        .build();
  }

  // 분류 규칙(Binding) — fanout이므로 라우팅 키 무시, 바인딩만 되어 있으면 전달됨
  @Bean
  public Binding otherFanoutBinding(Queue otherQueue, FanoutExchange otherFanoutExchange) {
    return BindingBuilder.bind(otherQueue).to(otherFanoutExchange);
  }
}
