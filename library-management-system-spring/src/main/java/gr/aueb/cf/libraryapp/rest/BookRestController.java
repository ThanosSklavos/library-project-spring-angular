package gr.aueb.cf.libraryapp.rest;

import gr.aueb.cf.libraryapp.dto.BookDTO;
import gr.aueb.cf.libraryapp.model.Book;
import gr.aueb.cf.libraryapp.model.Rental;
import gr.aueb.cf.libraryapp.model.User;
import gr.aueb.cf.libraryapp.service.IBookService;
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
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class BookRestController {

    private final IBookService bookService;

    @Autowired
    public BookRestController(IBookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Get books by their title or starting with initials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "books found",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BookDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid title supplied",
                    content = @Content) })
    @RequestMapping(path ="/books", method = RequestMethod.GET)
    public ResponseEntity<List<BookDTO>> getBooksByTitle(@RequestParam("title") String title) {
        List<Book> books;
        try {
            books = bookService.getBooksByTitle(title);
            List<BookDTO> booksDTO = new ArrayList<>();
            for (Book book : books) {
                booksDTO.add(map(book));
            }
            return new ResponseEntity<>(booksDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get all Books")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books found",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BookDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "No books found",
                    content = @Content) })
    @RequestMapping(path = "/books/getAll", method = RequestMethod.GET)
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<Book> books;

        try {
            books = bookService.getAllBooks();
            List<BookDTO> bookDTOs = new ArrayList<>();
            for (Book book : books) {
                bookDTOs.add(map(book));
            }
            return new ResponseEntity<>(bookDTOs, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
    }

    @Operation(summary = "Get a Book by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book found",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BookDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Id not found",
                    content = @Content) })
    @RequestMapping(path = "/books/{id}", method = RequestMethod.GET)
    public ResponseEntity<BookDTO> getBookById(@PathVariable("id") Long id) {
        try {
            Book book = bookService.getBookById(id);
            BookDTO bookDTO = mapWithRelations(book);
            return new ResponseEntity<>(bookDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Add a Book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book created",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BookDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "invalid input was supplied",
                    content = @Content) })
    @RequestMapping(path = "/books", method = RequestMethod.POST)
    public ResponseEntity<BookDTO> insertBook(@RequestBody BookDTO bookDTO) {

        try {
            Book book = bookService.insertBook(bookDTO);
            BookDTO bookDTO1 = map(book);

            return new ResponseEntity<>(bookDTO1, HttpStatus.OK);

        } catch (EntityAlreadyExistsException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Delete Book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book deleted",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BookDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Book not found",
                    content = @Content) })
    @RequestMapping(value = "/books/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<BookDTO> deleteBook(@PathVariable("id") Long bookId) {
        try {
            Book book = bookService.getBookById(bookId);
            bookService.removeBook(bookId);
            BookDTO bookDTO = map(book);
            return new ResponseEntity<>(bookDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Update Book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book updated",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BookDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Book not found",
                    content = @Content) })
    @RequestMapping(value = "/books/{id}", method = RequestMethod.PUT)
    public ResponseEntity<BookDTO> updateBook(@PathVariable("id") Long bookId,
                                              @RequestBody BookDTO dto) {
        try {
            dto.setId(bookId);
            Book book = bookService.updateBook(dto);

            BookDTO bookDTO = map(book);
            return new ResponseEntity<>(bookDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private BookDTO map(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setNumberOfCopies(book.getNumberOfCopies());
        dto.setAuthorLastname(book.getAuthor().getLastname());
        dto.setAuthorFirstname(book.getAuthor().getFirstname());
        return dto;
    }

    private BookDTO mapWithRelations(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setNumberOfCopies(book.getNumberOfCopies());
        dto.setAuthorLastname(book.getAuthor().getLastname());
        dto.setAuthorFirstname(book.getAuthor().getFirstname());
        List<User> users = new ArrayList<>();
        for (Rental rental : book.getRentals()) {
            users.add(rental.getUser());
        }
        dto.setRentByUser(users);

        return dto;
    }
}
