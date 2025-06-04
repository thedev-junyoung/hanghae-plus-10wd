package kr.hhplus.be.server.domain.outbox;


import java.time.LocalDateTime;
import java.util.List;

public interface OutboxRepository {
    List<OutboxMessage> findTop100ByIdGreaterThanOrderByIdAsc(String lastProcessedId);

    void save(OutboxMessage outboxEvent);

    List<OutboxMessage> findAll();

    boolean existsById(String eventId);

    void deleteAll();

    List<OutboxMessage> findTop100ByOccurredAtAfterOrderByOccurredAtAsc(LocalDateTime lastTime);
}
