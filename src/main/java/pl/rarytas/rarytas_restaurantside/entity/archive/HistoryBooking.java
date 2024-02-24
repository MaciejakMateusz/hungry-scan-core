package pl.rarytas.rarytas_restaurantside.entity.archive;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.rarytas.rarytas_restaurantside.entity.Booking;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "history_bookings")
@Entity
public class HistoryBooking {

    public HistoryBooking(Booking booking) {
        this.id = Long.valueOf(booking.getId());
        this.date = booking.getDate();
        this.time = booking.getTime();
        this.expirationTime = booking.getExpirationTime();
        this.numOfPpl = booking.getNumOfPpl();
        this.surname = booking.getSurname();
        this.tableId = booking.getTableId();
        this.numTablesBooked = booking.getNumTablesBooked();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull
    private LocalDate date;

    @Column(nullable = false)
    @NotNull
    private LocalTime time;

    @Column(nullable = false)
    @NotNull
    private LocalTime expirationTime;

    @Column(nullable = false)
    @NotNull
    @Min(1)
    private Short numOfPpl;

    @Column(nullable = false)
    @NotNull
    private String surname;

    @Column(nullable = false)
    @NotNull
    private Integer tableId;

    private Byte numTablesBooked;
}