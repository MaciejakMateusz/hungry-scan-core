package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import pl.rarytas.rarytas_restaurantside.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Slf4j
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 60)
    @NotBlank
    @Username
    @UsernameExists
    private String username;

    @Column(length = 100, nullable = false, unique = true)
    @NotBlank
    @Email
    @EmailExists
    private String email;

    @Column(nullable = false)
    @NotBlank
    @Password
    private String password;

    @Transient
    private String repeatedPassword;

    private LocalDateTime created;
    private LocalDateTime updated;

    private int enabled;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @PrePersist
    private void prePersist() {
        this.password = BCrypt.hashpw(this.password, BCrypt.gensalt());
        log.info("Password has been successfully encrypted.");
        this.created = LocalDateTime.now();
        log.info("Creation date has been set to : " + this.created);
    }

    @PreUpdate
    private void preUpdate() {
        this.updated = LocalDateTime.now();
        log.info("Date of edition has been set to : " + this.updated);
    }
}
