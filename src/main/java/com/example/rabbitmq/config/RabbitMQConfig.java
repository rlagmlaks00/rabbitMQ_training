package com.example.rabbitmq.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// ── 메시지 컨버터 ────────────────────────────
@Configuration
public class RabbitMQConfig {

  // Jackson 3 기반 JSON 컨버터 + 역직렬화 화이트리스트
  @Bean
  public MessageConverter jsonMessageConverter() {
    return new JacksonJsonMessageConverter("com.example.rabbitmq.messaging.dto");
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                       MessageConverter jsonMessageConverter) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(jsonMessageConverter);
    return template;
  }
}
