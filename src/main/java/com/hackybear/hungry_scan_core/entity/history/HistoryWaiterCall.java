package com.hackybear.hungry_scan_core.entity.history;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "history_waiter_calls")
public class HistoryWaiterCall {

    public HistoryWaiterCall(Long id,
                             Long tableId,
                             Integer tableNumber,
                             LocalDateTime callTime,
                             LocalDateTime resolvedTime,
                             boolean isResolved) {
        this.id = id;
        this.tableId = tableId;
        this.tableNumber = tableNumber;
        this.callTime = callTime;
        this.resolvedTime = resolvedTime;
        this.isResolved = isResolved;
    }

    @Id
    private Long id;

    private Long tableId;

    private Integer tableNumber;

    @Column(nullable = false)
    private LocalDateTime callTime;

    @Column(nullable = false)
    private LocalDateTime resolvedTime;

    @Column(nullable = false)
    private boolean isResolved = false;

}
