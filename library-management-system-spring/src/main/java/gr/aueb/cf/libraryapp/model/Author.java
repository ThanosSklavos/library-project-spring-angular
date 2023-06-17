package gr.aueb.cf.libraryapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "AUTHORS")
public class Author {
    @Id
    @Column(name = "ID", nullable = false, length = 225)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "FIRSTNAME", length = 225)
    private String firstname;

    @Column(name = "LASTNAME", nullable = false, length = 225)
    private String lastname;

    @JsonIgnore
    @OneToMany (mappedBy = "author", fetch = FetchType.EAGER)
    private Set<Book> books = new HashSet<>();

}
