package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "qr_scans")
public class QrScan {

    @Id
    private Integer id;

    @Column(name = "qr_code", nullable = false)
    private String qrCode;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    private Restaurant restaurant;

    @OneToOne
    @JoinColumn(name = "table_id", referencedColumnName = "id")
    private RestaurantTable restaurantTable;
}