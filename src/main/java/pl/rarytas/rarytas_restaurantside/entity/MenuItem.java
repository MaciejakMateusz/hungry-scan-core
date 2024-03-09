package pl.rarytas.rarytas_restaurantside.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "menu_items")
@Entity
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    @NotBlank
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @JsonIgnore
    private Category category;

    @Column(length = 500)
    @SizeIfNotEmpty
    @NotBlank
    private String description;

    @Column(length = 500)
    private String ingredients;

    @Column(nullable = false)
    @DecimalMin(value = "1", message = "Cena musi być większa od 1zł")
    @NotNull
    private BigDecimal price;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable = true;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    private String imageName;

    @PrePersist
    public void prePersist() {
        this.created = LocalDateTime.now();
        log.info("Time of creation has been set to: " + LocalDateTime.now());
    }

    @PreUpdate
    public void preUpdate() {
        this.updated = LocalDateTime.now();
        log.info("Time of update has been set to: " + LocalDateTime.now());
    }
}