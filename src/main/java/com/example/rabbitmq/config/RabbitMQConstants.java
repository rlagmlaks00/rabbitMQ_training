package com.example.rabbitmq.config;

public final class RabbitMQConstants {

  public static final String EXCHANGE_NAME = "order.exchange";

  public static final String QUEUE_NAME = "order.created.queue";
  public static final String OTHER_QUEUE_NAME = "other.new.queue";

  public static final String ROUTING_KEY = "order.created";

  public static final String FANOUT_EXCHANGE_NAME = "order.fanout.exchange";

  public static final String DLX_NAME = "order.dlx";
  public static final String DLQ_NAME = "order.dlq";
  public static final String DLQ_ROUTING_KEY = "order.failed";

  private RabbitMQConstants() {
  }
}
