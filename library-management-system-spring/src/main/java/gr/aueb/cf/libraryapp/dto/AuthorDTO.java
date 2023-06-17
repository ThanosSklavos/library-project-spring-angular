package gr.aueb.cf.libraryapp.dto;

import gr.aueb.cf.libraryapp.model.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDTO {
    private Long id;

    private String firstname;

    private String lastname;

    private Set<String> bookTitles = new HashSet<>();
}
