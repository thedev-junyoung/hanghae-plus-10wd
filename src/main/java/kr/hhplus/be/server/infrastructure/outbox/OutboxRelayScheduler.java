package kr.hhplus.be.server.infrastructure.outbox;

import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.outbox.OutBoxOffsetRepository;
import kr.hhplus.be.server.domain.outbox.OutboxMessage;
import kr.hhplus.be.server.domain.outbox.OutboxOffset;
import kr.hhplus.be.server.domain.outbox.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelayScheduler {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final OutBoxOffsetRepository offsetRepository;
    private static final LocalDateTime MYSQL_DATETIME_MIN = LocalDateTime.of(1970, 1, 1, 0, 0);

    public void relay() {
        log.info("[Outbox Relay 시작]");
        final String topic = "coupon.issue.requested";
        OutboxOffset offset = offsetRepository.findById(topic)
                .orElse(OutboxOffset.create(topic, MYSQL_DATETIME_MIN));

        LocalDateTime lastTime = offset.getLastProcessedOccurredAt();

        while (true) {
            List<OutboxMessage> messages = outboxRepository.findTop100ByOccurredAtAfterOrderByOccurredAtAsc(lastTime);
            log.info("[Outbox 메시지 조회] 조회된 메시지 수: {}, 기준 시간: {}", messages.size(), lastTime);
            if (messages.isEmpty()) break;

            for (OutboxMessage message : messages) {
                try {
                    kafkaTemplate.send(topic, message.getAggregateId(), message.getPayload());
                    log.info("[Kafka 전송 성공] id={}, topic={}", message.getId(), topic);
                } catch (Exception e) {
                    log.error("[Kafka 전송 실패] id={}, topic={}", message.getId(), topic, e);
                    break; // 중단: 이후 메시지 처리 안 함
                }
            }

            // ✅ 처리한 메시지들의 마지막 occurredAt을 기준으로 offset 갱신
            lastTime = messages.get(messages.size() - 1).getOccurredAt();
            offset.updateLastProcessedOccurredAt(lastTime);
            offsetRepository.save(offset);
        }
    }


}
