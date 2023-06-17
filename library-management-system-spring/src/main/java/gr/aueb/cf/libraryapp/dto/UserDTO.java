package gr.aueb.cf.libraryapp.dto;

import gr.aueb.cf.libraryapp.model.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;

    private String username;

    private String firstname;

    private String lastname;

    private String email;

    private String password;

//    private Set<Rental> rentals;
    private List<BookDTO> booksRented;

}
