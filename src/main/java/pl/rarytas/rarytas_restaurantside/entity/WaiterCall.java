package pl.rarytas.rarytas_restaurantside.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "waiter_calls")
public class WaiterCall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @Column(name = "call_time", nullable = false)
    private LocalDateTime callTime;

    @Column(name = "is_resolved", nullable = false)
    private boolean isResolved = false;

    @PrePersist
    private void prePersist() {
        this.callTime = LocalDateTime.now();
    }
}