package gr.aueb.cf.libraryapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "BOOKS")
public class Book {

    @Id
    @Column(name = "ID", nullable = false, length = 225)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "TITLE", nullable = false, length = 225)
    private String title;

    @Column(name = "NUMBER_OF_COPIES", length = 225)
    private int numberOfCopies;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "AUTHOR_FK", nullable = false)
    private Author author;

    @JsonIgnore
    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER)
    private Set<Rental> rentals;


}
