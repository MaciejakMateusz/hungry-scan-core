package pl.rarytas.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.hungry_scan_core.listener.GeneralListener;
import pl.rarytas.hungry_scan_core.utility.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "variants")
@EntityListeners(GeneralListener.class)
@Entity
public class MenuItemVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    @NotBlank
    private String name;

    @Column(nullable = false)
    private BigDecimal price = Money.of(0.00);

    private boolean isAvailable = true;

    private boolean isDefaultVariant;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;
}