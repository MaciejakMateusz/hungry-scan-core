package com.hackybear.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hackybear.hungry_scan_core.enums.BillingPeriod;
import com.hackybear.hungry_scan_core.enums.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "price_plans")
@Entity
public class PricePlan implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;

    @OneToOne
    @JoinColumn(name = "restaurant_id")
    @JsonIgnore
    private Restaurant restaurant;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "plan_type_id", nullable = false)
    private PricePlanType planType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate activationDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate renewalDate;

    @Enumerated(EnumType.STRING)
    private BillingPeriod billingPeriod;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
}