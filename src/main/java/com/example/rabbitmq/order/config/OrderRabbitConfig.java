package com.example.rabbitmq.order.config;

import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static com.example.rabbitmq.config.RabbitMQConstants.EXCHANGE_NAME;
import static com.example.rabbitmq.config.RabbitMQConstants.FANOUT_EXCHANGE_NAME;
import static com.example.rabbitmq.config.RabbitMQConstants.QUEUE_NAME;
import static com.example.rabbitmq.config.RabbitMQConstants.ROUTING_KEY;
import static com.example.rabbitmq.config.RabbitTopologySupport.binding;
import static com.example.rabbitmq.config.RabbitTopologySupport.deadLetteredQueue;

// ── 주문 도메인: order.exchange(topic) + order.fanout.exchange(fanout) + order.created.queue ──
// fanout exchange는 다른 도메인(Other 등)도 브로드캐스트 대상으로 바인딩하므로 여기서 선언한다.
@Configuration
public class OrderRabbitConfig {

  @Bean
  public Declarables orderRabbitTopology() {
    List<Declarable> declarables = List.of(
        new TopicExchange(EXCHANGE_NAME),
        new FanoutExchange(FANOUT_EXCHANGE_NAME),
        deadLetteredQueue(QUEUE_NAME),
        binding(EXCHANGE_NAME, QUEUE_NAME, ROUTING_KEY),   // topic: 라우팅 키 정확히 매칭
        binding(FANOUT_EXCHANGE_NAME, QUEUE_NAME, "")      // fanout: 키 무시, 브로드캐스트 수신
    );
    return new Declarables(declarables);
  }
}
