package com.hackybear.hungry_scan_core.entity;

import com.hackybear.hungry_scan_core.utility.Money;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "price_plans")
@Entity
public class PricePlan implements Serializable {

    @Id
    private String id;

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    @NotNull
    private BigDecimal price = Money.of(0.0);

}