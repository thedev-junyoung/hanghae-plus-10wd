# STEP 19. 성능 테스트 시나리오 & 실행 계획 (쿠폰 비동기 발급 API)

## ✅ 테스트 대상

- `POST /api/v1/coupons/limited-issue/async`
- Kafka 기반 비동기 쿠폰 발급 구조

## ✅ 목적

- 초당 수천 건 요청이 들어올 때도 Kafka 기반 구조가 안정적으로 처리 가능한지 검증
- TPS, 응답 시간, Kafka Lag 등 성능 지표 기반 병목 요소 분석

## ✅ 테스트 환경

| 항목 | 구성 |
|------|------|
| 부하 도구 | k6 (`vus = 50`, `duration = 30s`) |
| 인프라 | Kafka, Redis Sentinel, Prometheus, Grafana, Spring Boot App |
| 비동기 흐름 | 컨트롤러 → Kafka Producer → Kafka Consumer → 쿠폰 발급 처리 |
| 관측 지표 | TPS, 응답 시간, Kafka 처리량, Kafka Consumer Lag 등 |

## ✅ 테스트 시나리오

- 50명의 가상 사용자가 30초 동안 동시에 쿠폰 발급 요청
- 요청에는 유효한 쿠폰 코드 및 사용자 ID 포함
- Kafka Producer는 요청을 수락하고, Consumer가 백그라운드에서 처리
- DLQ 설정 포함

## ✅ 테스트 설계 요약

- 요청이 빠르게 수락되더라도 실제 발급은 Kafka Consumer의 처리 속도에 따라 달라짐
- Kafka Lag, DLQ 유무, Consumer 처리 속도 중심으로 시스템 안정성 평가
- 클라이언트는 즉시 응답을 받지만, 발급 성공 여부는 비동기 처리됨
