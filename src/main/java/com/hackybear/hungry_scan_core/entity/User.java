package com.hackybear.hungry_scan_core.entity;

import com.hackybear.hungry_scan_core.annotation.Email;
import com.hackybear.hungry_scan_core.annotation.Password;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private Long organizationId;

    @Column(length = 100, nullable = false, unique = true)
    @NotBlank
    @Email
    private String username;

    private String name;
    private String surname;
    private String phoneNumber;

    @Email
    private String email;

    @Column(nullable = false)
    @NotBlank
    @Password
    private String password;

    @Transient
    private String repeatedPassword;

    @Column(length = 36)
    private String emailToken;

    @OneToOne(cascade = CascadeType.ALL)
    private JwtToken jwtToken;

    private LocalDateTime created;
    private LocalDateTime updated;

    private int enabled = 1;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @NotEmpty
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_restaurants", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "restaurant_id"))
    private Set<Restaurant> restaurants = new HashSet<>();

    public void addRestaurant(Restaurant restaurant) {
        restaurants.add(restaurant);
    }

    public void addAllRestaurants(Set<Restaurant> restaurants) {
        this.restaurants.addAll(restaurants);
    }

    public void removeRestaurant(Restaurant restaurant) {
        restaurants.remove(restaurant);
    }

    @PrePersist
    private void prePersist() {
        this.password = BCrypt.hashpw(this.password, BCrypt.gensalt());
        log.info("Password has been successfully encrypted.");
        this.created = LocalDateTime.now();
        log.info("Creation date has been set to : {}", this.created);
    }

    @PreUpdate
    private void preUpdate() {
        this.updated = LocalDateTime.now();
        log.info("Date of edition has been set to : {}", this.updated);
    }
}
