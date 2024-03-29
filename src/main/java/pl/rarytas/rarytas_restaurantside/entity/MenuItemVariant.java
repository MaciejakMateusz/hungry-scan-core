package pl.rarytas.rarytas_restaurantside.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.rarytas_restaurantside.annotation.SizeIfNotEmpty;
import pl.rarytas.rarytas_restaurantside.utility.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "menu_item_variants")
@Entity
public class MenuItemVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    @NotBlank
    private String name;

    @Column(nullable = false)
    @DecimalMin(value = "1", message = "Cena musi być większa od 1zł")
    @NotNull
    private BigDecimal price;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable = true;

    private boolean isDefaultVariant;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    @PrePersist
    public void prePersist() {
        this.created = LocalDateTime.now();
        log.info("Time of menu item variant creation has been set to: " + LocalDateTime.now());
    }

    @PreUpdate
    public void preUpdate() {
        this.updated = LocalDateTime.now();
        log.info("Time of menu item variant update has been set to: " + LocalDateTime.now());
    }
}