package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@EqualsAndHashCode
@Table(name = "restaurants")
@Entity
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false)
    @NotBlank
    private String name;

    @Column(length = 300)
    private String address;

    private LocalDateTime created;
    private LocalDateTime updated;

    @Min(1)
    @NotNull
    @Column(nullable = false)
    private short capacity;

    @NotNull
    @Column(nullable = false)
    private LocalTime opening;

    @NotNull
    @Column(nullable = false)
    private LocalTime closing;

    @PrePersist
    private void prePersist() {
        this.created = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        this.updated = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return name + ", " + address;
    }

}