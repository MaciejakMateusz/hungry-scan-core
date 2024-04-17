package pl.rarytas.rarytas_restaurantside.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.rarytas.rarytas_restaurantside.enums.PaymentMethod;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "bill_parts")
@Entity
public class BillPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderedItem> orderedItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private BigDecimal partValue;

}
