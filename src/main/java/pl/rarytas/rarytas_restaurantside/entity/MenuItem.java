package pl.rarytas.rarytas_restaurantside.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "menu_items")
@Slf4j
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    @NotEmpty
    @NotNull
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @JsonIgnore
    private Category category;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String ingredients;

    @Column(nullable = false)
    @Min(value = 1, message = "Cena musi być większa od 1zł")
    @NotNull
    @NotEmpty
    private BigDecimal price;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable = true;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB")
    @JsonIgnore
    private byte[] image;

    @Transient
    @JsonIgnore
    private MultipartFile imageFile;

    @Transient
    @JsonInclude
    private String base64Image;

    @PrePersist
    public void prePersist() throws IOException {
        this.created = LocalDateTime.now();
        log.info("Time of creation has been set to: " + LocalDateTime.now());
        if (this.imageFile != null) {
            this.image = this.imageFile.getBytes();
            log.info(this.imageFile.getOriginalFilename() + " has been converted into byte array.");
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updated = LocalDateTime.now();
        log.info("Time of update has been set to: " + LocalDateTime.now());
    }

    public String getBase64Image() {
        if (Objects.isNull(image)) {
            return "empty";
        }
        return Base64.getEncoder().encodeToString(image);
    }

}