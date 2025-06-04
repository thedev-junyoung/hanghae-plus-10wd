package kr.hhplus.be.server.domain.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "outbox_offset")
public class OutboxOffset {
    @Id
    @Column(nullable = false, unique = true, name = "topic_name")
    private String topicName;

    @Column(nullable = false, name = "last_processed_occurred_at")
    private LocalDateTime lastProcessedOccurredAt;

    public static OutboxOffset create(String topic, LocalDateTime init) {
        OutboxOffset o = new OutboxOffset();
        o.topicName = topic;
        o.lastProcessedOccurredAt = init;
        return o;
    }

    public void updateLastProcessedOccurredAt(LocalDateTime time) {
        this.lastProcessedOccurredAt = time;
    }


}

