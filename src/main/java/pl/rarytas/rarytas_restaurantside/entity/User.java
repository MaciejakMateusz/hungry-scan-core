package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import pl.rarytas.rarytas_restaurantside.annotation.Email;
import pl.rarytas.rarytas_restaurantside.annotation.Password;

import java.time.LocalDateTime;

@Entity
@Slf4j
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false, unique = true)
    @NotBlank
    @Email
    private String email;

    @Column(length = 50, nullable = false)
    @NotBlank
    private String firstName;

    @Column(length = 75, nullable = false)
    @NotBlank
    private String lastName;

    @Column(nullable = false)
    @NotBlank
    @Password
    private String password;

    @Transient
    private String repeatedPassword;

    private LocalDateTime created;
    private LocalDateTime updated;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin = false;

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
