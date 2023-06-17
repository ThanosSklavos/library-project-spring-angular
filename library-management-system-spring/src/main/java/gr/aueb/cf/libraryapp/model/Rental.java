package gr.aueb.cf.libraryapp.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "RENTALS")
public class Rental {

    @Id
    @Column(name = "ID", nullable = false, length = 225)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_FK", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "BOOK_FK", nullable = false)
    private Book book;

    @Column(name = "DATE_OF_RENTAL", nullable = false)
    private LocalDate rentalDate;
}
