package pl.rarytas.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.hungry_scan_core.listener.GeneralListener;
import pl.rarytas.hungry_scan_core.utility.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "ingredients")
@EntityListeners(GeneralListener.class)
@Entity
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    @NotBlank
    private String name;

    @Column(nullable = false)
    @NotNull
    private BigDecimal price = Money.of(0.00);

    @Column(nullable = false)
    private boolean isAvailable = true;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    public BigDecimal getPrice() {
        return this.price.setScale(2, RoundingMode.HALF_UP);
    }

    public void setPrice(BigDecimal totalAmount) {
        this.price = totalAmount.setScale(2, RoundingMode.HALF_UP);
    }

}