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
@Table(name = "USERS")
public class User {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "USERNAME", nullable = false, length = 225)
    private String username;

    @Column(name = "FIRSTNAME", nullable = false, length = 225)
    private String firstname;

    @Column(name = "LASTNAME", nullable = false, length = 225)
    private String lastname;

    @Column(name = "EMAIL", length = 225)
    private String email;

    @Column(name = "PASSWORD", nullable = false, length = 225)
    private String password;


    //TODO FetchType should be .LAZY
    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Rental> rentals;


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", rentals=" + rentals +
                '}';
    }
}
