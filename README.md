# RabbitMQ 설정 가이드

이 프로젝트는 Spring Boot(AMQP) + RabbitMQ 3노드 클러스터로 구성되어 있으며,
Exchange/Queue/Binding은 헥사고날 아키텍처의 각 도메인(config) 패키지에서 코드로 선언(Declarables)합니다.

## 1. 클러스터 실행 (docker-compose)

`docker-compose.yaml`은 RabbitMQ 3노드 클러스터를 구성합니다.

```bash
docker-compose up -d
```

| 노드 | AMQP 포트 | 관리 UI 포트 | 비고 |
|---|---|---|---|
| rabbitmq1 | 5672 | 15672 | 첫 노드, `RABBITMQ_DEFAULT_USER/PASS`로 guest/guest 생성 |
| rabbitmq2 | 5673 | 15673 | 기동 후 `rabbitmqctl join_cluster rabbit@rabbitmq1` 로 클러스터 조인 |
| rabbitmq3 | 5674 | 15674 | rabbitmq2와 동일한 방식으로 조인 |

- **`RABBITMQ_ERLANG_COOKIE`**: 클러스터로 묶일 노드들은 반드시 동일한 값을 가져야 합니다.
- **`depends_on: condition: service_healthy`**: rabbitmq2/3은 rabbitmq1의 healthcheck(`rabbitmq-diagnostics ping`) 통과 후에만 기동 및 조인을 시도합니다.
- **볼륨(`rabbitmqN_data`)**: 컨테이너 재시작 시 데이터(큐, 메시지 등) 유실을 막기 위해 필수입니다.
- 관리 UI는 `http://localhost:15672` (guest/guest)에서 확인할 수 있습니다.

## 2. 브로커 설정 (`rabbitmq.conf`)

```
loopback_users = none
```

`guest` 계정은 기본적으로 loopback(localhost) 연결만 허용합니다. Docker 포트포워딩을 통해
호스트에서 접속하면 브로커 입장에서는 non-loopback 연결로 보여 `ACCESS_REFUSED`가 발생하므로,
로컬 테스트 편의를 위해 이 제약을 해제했습니다. **운영 환경에서는 guest 대신 별도 계정을 사용하고
이 설정을 제거해야 합니다.**

## 3. 애플리케이션 설정 (`application.yaml`)

```yaml
spring:
  rabbitmq:
    addresses: localhost:5672,localhost:5673,localhost:5674
    username: guest
    password: guest
    listener:
      simple:
        retry:
          enabled: true
          max-attempts: 3
          initial-interval: 1000ms
          multiplier: 2.0
          max-interval: 10000ms
```

- **`addresses`**: 단일 `host:port` 대신 3개 노드 주소를 나열해, 앞 노드가 죽으면 다음 주소로 자동 페일오버합니다.
- **`listener.simple.retry`**: 리스너에서 예외 발생 시 1초 → 2초 → 4초 간격으로 최대 3회 재시도합니다.
  (참고로 `prefetch`는 주석 처리되어 있으며, 활성화하면 ack 전까지 한 번에 1개 메시지만 배달받습니다.)

## 4. 메시지 컨버터 (`RabbitMQConfig`)

- Jackson 3 기반 `JacksonJsonMessageConverter`를 사용해 메시지를 JSON으로 직렬화/역직렬화합니다.
- 역직렬화 허용 패키지를 `com.example.rabbitmq.order.domain.model`로 제한한 화이트리스트를 적용해 임의 클래스 역직렬화를 방지합니다.
- `RabbitTemplate`에 위 컨버터를 설정하여 발행 시에도 동일한 방식으로 직렬화합니다.

## 5. 토폴로지(Exchange/Queue/Binding) 선언 방식

각 도메인 패키지의 `*RabbitConfig`가 `Declarables` 빈으로 자신이 소유한 Exchange/Queue/Binding을
직접 선언합니다. 공통 규칙은 `RabbitTopologySupport`에 모아두었습니다.

- `RabbitTopologySupport.deadLetteredQueue(name)`: 실패 시 공용 DLX(`order.dlx`)로 데드레터링되는 durable quorum queue를 생성합니다.
- `RabbitTopologySupport.plainQueue(name)`: 데드레터링되지 않는 일반 durable quorum queue를 생성합니다.
- `RabbitTopologySupport.binding(exchange, queue, routingKey)`: Exchange/Queue 빈을 직접 주입받지 않고 이름만으로 바인딩을 선언해, 빈 이름 불일치로 인한 우발적 매칭을 방지합니다.

상수는 `RabbitMQConstants`에 모아두었습니다.

### 5.1 주문(order) 도메인 — `OrderRabbitConfig`

| 종류 | 이름 | 타입 |
|---|---|---|
| Exchange | `order.exchange` | Topic |
| Exchange | `order.fanout.exchange` | Fanout |
| Queue | `order.created.queue` | quorum, DLX 연결(`order.dlx`) |

- `order.exchange` → `order.created.queue` : 라우팅 키 `order.created`로 정확히 매칭(topic).
- `order.fanout.exchange` → `order.created.queue` : 라우팅 키 무시, 브로드캐스트 수신(fanout).
- `order.fanout.exchange`는 다른 도메인(other)도 구독하므로 order 패키지에서 소유·선언합니다.

### 5.2 기타(other) 도메인 — `OtherRabbitConfig`

| 종류 | 이름 | 타입 |
|---|---|---|
| Queue | `other.new.queue` | quorum, DLQ 미연결(plain) |

- `order.fanout.exchange` → `other.new.queue` : fanout 브로드캐스트 수신. Exchange는 이름으로만 참조(소유는 order 패키지).

### 5.3 DLQ(Dead Letter) — `DeadLetterRabbitConfig`

| 종류 | 이름 | 타입 |
|---|---|---|
| Exchange | `order.dlx` | Direct |
| Queue | `order.dlq` | plain quorum queue |

- `order.dlx` → `order.dlq` : 라우팅 키 `order.failed`.
- `order.created.queue`에서 처리 실패(재시도 소진 등)한 메시지가 `order.dlx`를 거쳐 `order.dlq`로 이동합니다.

## 6. 발행(Producer) / 구독(Consumer)

- `OrderEventProducer` (`OrderEventPublisher` 구현체): `order.exchange`에 라우팅 키 `order.created`로 발행 (topic).
- `BroadcastEventProducer` (`OrderEventBroadcastPublisher` 구현체): `order.fanout.exchange`에 라우팅 키 없이 발행 (fanout).
- `OrderConsumer`: `order.created.queue`를 구독해 `HandleOrderCreatedUseCase`로 위임.
- `OtherConsumer`: `other.new.queue`를 구독해 `HandleOtherCreatedUseCase`로 위임.

## 7. 전체 메시지 흐름 요약

```
                         ┌─ order.exchange (topic, key=order.created) ──▶ order.created.queue ─▶ OrderConsumer
OrderService ─(이벤트 발행)─┤
                         └─ order.fanout.exchange (fanout) ──┬─▶ order.created.queue ─▶ OrderConsumer
                                                              └─▶ other.new.queue     ─▶ OtherConsumer

order.created.queue 처리 실패 ──▶ order.dlx (direct, key=order.failed) ──▶ order.dlq
```
