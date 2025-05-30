package com.hackybear.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hackybear.hungry_scan_core.listener.GeneralListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "restaurant_tables")
@EntityListeners({AuditingEntityListener.class, GeneralListener.class})
@Entity
public class RestaurantTable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer number;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<User> users;

    @OneToMany(fetch = FetchType.EAGER)
    private List<WaiterCall> waiterCalls = new ArrayList<>();

    @ManyToOne
    private Zone zone;

    private String qrName;

    private boolean hasQrCode = false;

    private boolean isActive;

    private boolean isVisible = true;

    private boolean billRequested;

    private boolean waiterCalled;

    @Min(value = 1)
    private int maxNumOfPpl;

    @Column(length = 36, nullable = false)
    @NotNull
    private String token;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    @LastModifiedBy
    private String modifiedBy;

    @CreatedBy
    private String createdBy;

    public void addCustomer(User user) {
        this.users.add(user);
    }

}