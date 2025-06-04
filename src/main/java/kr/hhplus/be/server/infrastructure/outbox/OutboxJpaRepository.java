package kr.hhplus.be.server.infrastructure.outbox;

import kr.hhplus.be.server.domain.outbox.OutboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OutboxJpaRepository extends JpaRepository<OutboxMessage, String> {
    List<OutboxMessage> findTop100ByIdGreaterThanOrderByIdAsc(String lastProcessedId);

    Optional<OutboxMessage> findTop100ByOccurredAtAfterOrderByOccurredAtAsc(LocalDateTime lastTime);
}