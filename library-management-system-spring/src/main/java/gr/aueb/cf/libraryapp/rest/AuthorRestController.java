package gr.aueb.cf.libraryapp.rest;

import gr.aueb.cf.libraryapp.dto.AuthorDTO;
import gr.aueb.cf.libraryapp.dto.BookDTO;
import gr.aueb.cf.libraryapp.dto.UserDTO;
import gr.aueb.cf.libraryapp.model.Author;
import gr.aueb.cf.libraryapp.model.Book;
import gr.aueb.cf.libraryapp.service.IAuthorService;
import gr.aueb.cf.libraryapp.service.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.libraryapp.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.libraryapp.service.util.LoggerUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class AuthorRestController {
    /**
     * Not yet used in app.
     */
    private final IAuthorService authorService;

    @Autowired
    public AuthorRestController(IAuthorService authorService) {
        this.authorService = authorService;
    }

    @Operation(summary = "Add an Author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Author created",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthorDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "invalid input was supplied",
                    content = @Content) })
    @RequestMapping(path = "/authors", method = RequestMethod.POST)
    public ResponseEntity<AuthorDTO> registerAuthor(@RequestBody AuthorDTO authorDTO) {


        try {
            Author author = authorService.insertAuthor(authorDTO);
            AuthorDTO authorDTO1 = map(author);


            return new ResponseEntity<>(authorDTO1, HttpStatus.OK);

        } catch (EntityAlreadyExistsException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Delete Author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Author deleted",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Author not found",
                    content = @Content) })
    @RequestMapping(value = "/authors/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<AuthorDTO> deleteAuthor(@PathVariable("id") Long authorId) {
        try {
            Author author = authorService.getAuthorById(authorId);
            authorService.deleteAuthor(authorId);
            AuthorDTO authorDTO = map(author);

            return new ResponseEntity<>(authorDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get authors by their lastname or starting with initials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Author found",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthorDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid lastname supplied",
                    content = @Content) })
    @RequestMapping(path ="/authors", method = RequestMethod.GET)
    public ResponseEntity<List<AuthorDTO>> getAuthorsByTitle(@RequestParam("lastname") String lastname) {
        List<Author> authors;
        try {
            authors = authorService.getAuthorByLastname(lastname);
            List<AuthorDTO> authorsDTO = new ArrayList<>();
            for (Author author : authors) {
                authorsDTO.add(map(author));
            }
            return new ResponseEntity<>(authorsDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private AuthorDTO map(Author author) {
        AuthorDTO dto = new AuthorDTO();
        dto.setId(author.getId());
        dto.setLastname(author.getLastname());
        dto.setFirstname(author.getFirstname());

        Set<String> bookTitles = new HashSet<>();
        for (Book book : author.getBooks()) {
            bookTitles.add(book.getTitle());
        }
        dto.setBookTitles(bookTitles);
        return dto;
    }
}
