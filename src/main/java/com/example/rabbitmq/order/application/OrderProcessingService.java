package com.example.rabbitmq.order.application;

import com.example.rabbitmq.order.domain.model.OrderCreatedEvent;
import com.example.rabbitmq.order.domain.port.in.HandleOrderCreatedUseCase;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessingService implements HandleOrderCreatedUseCase {

  @Override
  public void handle(OrderCreatedEvent event) throws InterruptedException {
    System.out.println("주문 처리 시작: " + event.orderId());
//        throw new RuntimeException("일부러 실패시켜서 DLQ 테스트");
    Thread.sleep(2000); // 관리 콘솔에서 소비 과정을 눈으로 확인하기 위한 의도적 지연
    // 실제 비즈니스 로직 (재고 차감, 알림 발송 등)
    System.out.println("주문 처리 완료: " + event.orderId());
  }
}
