package gr.aueb.cf.libraryapp.dto;

import gr.aueb.cf.libraryapp.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    private Long id;

    private String title;

    private int numberOfCopies;

    private String authorLastname;
    private String authorFirstname;
    private LocalDate rentedDate;

    private List<User> rentByUser = new ArrayList<>();
}
