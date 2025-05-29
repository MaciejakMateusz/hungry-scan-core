package com.hackybear.hungry_scan_core.entity;

import com.hackybear.hungry_scan_core.utility.Money;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "price_plan_types")
@Entity
public class PricePlanType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String name;

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    @NotNull
    private BigDecimal price = Money.of(0.0);

}