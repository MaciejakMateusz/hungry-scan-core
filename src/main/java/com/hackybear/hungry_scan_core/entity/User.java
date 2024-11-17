package com.hackybear.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hackybear.hungry_scan_core.annotation.Email;
import com.hackybear.hungry_scan_core.annotation.Password;
import com.hackybear.hungry_scan_core.listener.GeneralListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Getter
@Setter
@EqualsAndHashCode
@EntityListeners({GeneralListener.class, AuditingEntityListener.class})
@Table(name = "users")
@Entity
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    private Long organizationId;

    @Column(length = 100, nullable = false, unique = true)
    @NotBlank
    @Email
    private String username;

    @NotBlank
    private String name;

    @NotBlank
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

    @Column(length = 36, unique = true)
    private String emailToken;

    private LocalDateTime emailTokenExpiry;

    @OneToOne(cascade = CascadeType.ALL)
    private JwtToken jwtToken;

    private int enabled = 0;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "users_restaurants", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "restaurant_id"))
    private Set<Restaurant> restaurants = new HashSet<>();

    @Column(name = "active_restaurant_id")
    private Long activeRestaurantId;

    @Column(name = "active_menu_id")
    private Long activeMenuId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    @LastModifiedBy
    private String modifiedBy;

    @CreatedBy
    private String createdBy;

    public void addRestaurant(Restaurant restaurant) {
        restaurants.add(restaurant);
    }

    @PrePersist
    private void prePersist() {
        this.password = BCrypt.hashpw(this.password, BCrypt.gensalt());
    }

    @PreUpdate
    private void preUpdate() {
        this.emailTokenExpiry = LocalDateTime.now().plusMinutes(15);
    }

}
