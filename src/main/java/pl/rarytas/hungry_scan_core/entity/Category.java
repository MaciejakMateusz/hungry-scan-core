package pl.rarytas.hungry_scan_core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import pl.rarytas.hungry_scan_core.annotation.SizeIfNotEmpty;
import pl.rarytas.hungry_scan_core.listener.GeneralListener;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@EntityListeners(GeneralListener.class)
@Table(name = "categories")
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false)
    @NotBlank
    private String name;

    @SizeIfNotEmpty
    @Length(max = 300)
    @Column(length = 300)
    private String description;

    private boolean isAvailable = true;

    @Min(1)
    private Integer displayOrder;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    @Override
    public String toString() {
        return name;
    }
}
