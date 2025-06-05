# STEP 19. 성능 테스트 시나리오 & 실행 계획 (주문 생성 API)

## ✅ 테스트 대상

- `POST /api/v1/orders`
- Kafka 기반 재고 차감 이벤트 기반 아키텍처

## ✅ 목적

- 주문 처리 API의 확장성과 안정성 검증
- Kafka를 활용한 재고 차감 비동기 처리 구조의 성능과 병목 요소 식별

## ✅ 테스트 환경

| 항목 | 구성 |
|------|------|
| 부하 도구 | k6 (`vus = 50`, `duration = 30s`) |
| 인프라 | Kafka, Redis Sentinel, Prometheus, Grafana, Spring Boot App |
| 이벤트 흐름 | 주문 생성 → StockDecreaseRequested 이벤트 발행 → Kafka 전송 → Kafka Consumer 재고 차감 처리 |
| 관측 지표 | TPS, 응답 시간, Kafka 메시지 처리량, Consumer 처리량, Lag 등 |

## ✅ 테스트 시나리오

- 50명의 가상 사용자가 30초 동안 무작위 상품으로 주문 요청을 보냄
- 쿠폰 `WELCOME10` 포함, 무작위 유저 ID 및 상품 구성
- Kafka로 재고 차감 이벤트 발행
- Consumer가 수신하여 실제 재고 처리 수행

## ✅ 테스트 설계 요약

- Kafka 메시지 유입량과 Consumer 처리량을 비교하여 병목 여부 확인
- Consumer 처리 실패나 Lag이 발생하는 경우 보상 로직이 동작해야 함
- Spring 이벤트 → Kafka Producer → Kafka Listener 구조의 성능 관찰
