package pl.rarytas.rarytas_restaurantside.entity.history;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "history_waiter_calls")
public class HistoryWaiterCall {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "history_order_id", referencedColumnName = "id")
    private HistoryOrder historyOrder;

    @Column(name = "call_time", nullable = false)
    private LocalDateTime callTime;

    @Column(name = "resolved_time", nullable = false)
    private LocalDateTime resolvedTime;

    @Column(name = "is_resolved", nullable = false)
    private boolean isResolved = false;

}
