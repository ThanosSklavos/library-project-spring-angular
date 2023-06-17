package gr.aueb.cf.libraryapp.rest;

import gr.aueb.cf.libraryapp.authentication.CustomAuthenticationProvider;
import gr.aueb.cf.libraryapp.dto.BookDTO;
import gr.aueb.cf.libraryapp.dto.LoginDTO;
import gr.aueb.cf.libraryapp.dto.UserDTO;
import gr.aueb.cf.libraryapp.model.Book;
import gr.aueb.cf.libraryapp.model.Rental;
import gr.aueb.cf.libraryapp.model.User;
import gr.aueb.cf.libraryapp.repository.BookRepository;
import gr.aueb.cf.libraryapp.service.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.libraryapp.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.libraryapp.service.IUserService;
import gr.aueb.cf.libraryapp.service.exceptions.OutOfStockException;
import gr.aueb.cf.libraryapp.service.util.LoggerUtil;
import gr.aueb.cf.libraryapp.validator.UserValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class UserRestController {

    private final IUserService userService;
    private final BookRepository bookRepository;
    private final UserValidator userValidator;
    private MessageSourceAccessor accessor;
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private HttpSession httpSession;

    @Autowired
    public UserRestController(IUserService userService, BookRepository bookRepository, UserValidator userValidator, CustomAuthenticationProvider customAuthenticationProvider,HttpSession httpSession) {
        this.userService = userService;
        this.bookRepository = bookRepository;
        this.userValidator = userValidator;
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.httpSession = httpSession;
    }


    //TODO Configure the session principal, right now an authenticated user can not be added. (this is a stateless app).
    @Operation(summary = "Login and set this user as Principal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LoginDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "id = null",
                    content = @Content) })
    @RequestMapping(value = "/users/login", method = RequestMethod.POST)
    public ResponseEntity<LoginDTO> userLogin(@RequestBody LoginDTO loginDTO) {
        try {
            Authentication auth = customAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(auth);
            this.httpSession.setAttribute("auth", auth);
            System.out.println(auth);

            LoginDTO loginDTO1 = userService.isUserValid(loginDTO);
            return new ResponseEntity<>(loginDTO1, HttpStatus.OK);
        } catch (AuthenticationException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @Operation(summary = "Logout and delete Principal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LoginDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "id = null",
                    content = @Content) })
    @RequestMapping(value = "/users/logout", method = RequestMethod.DELETE)
    public ResponseEntity<Void> userLogout(HttpServletRequest request) {
        System.out.println(SecurityContextHolder.getContext().getAuthentication());

        System.out.println(this.httpSession.getAttribute("auth"));

        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logoutHandler.logout(request, null, auth);
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Get users by their username or starting with initials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid username supplied",
                    content = @Content) })
    @RequestMapping(path ="/users", method = RequestMethod.GET)
    public ResponseEntity<List<UserDTO>> getUsersByUsername(@RequestParam("username") String username) {
        List<User> users;
        try {
            users = userService.getUsersByUsername(username);
            List<UserDTO> usersDTO = new ArrayList<>();
            for (User user : users) {
                usersDTO.add(mapWithRentals(user));
            }
            return new ResponseEntity<>(usersDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get a User by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Id not found",
                    content = @Content) })
    @RequestMapping(path = "/users/{id}", method = RequestMethod.GET)
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id) {
        try {
            User user = userService.getUserById(id);
            UserDTO userDTO = mapWithRentals(user);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get all Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "No Users found",
                    content = @Content) })
    @RequestMapping(path = "/users/getAll", method = RequestMethod.GET)
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users;

        try {
            users = userService.getAllUsers();
            List<UserDTO> userDTOs = new ArrayList<>();
            for (User user : users) {
                userDTOs.add(mapWithRentals(user));
            }
            return new ResponseEntity<>(userDTOs, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Add a User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "invalid input was supplied",
                    content = @Content) })
    @RequestMapping(path = "/users", method = RequestMethod.POST)
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO, BindingResult bindingResult) {
        userValidator.validate(userDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("empty"));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            User user = userService.insert(userDTO);
            UserDTO userDTO1 = map(user);

            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(userDTO1.getId())
                    .toUri();

            return ResponseEntity.created(location).body(userDTO1);

        } catch (EntityAlreadyExistsException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Delete User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "User not found",
                    content = @Content) })
    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<UserDTO> deleteUser(@PathVariable("id") Long userId) {
        try {
            User user = userService.getUserById(userId);
            userService.delete(userId);
            UserDTO userDTO = map(user);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Update User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "User not found",
                    content = @Content) })
    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") Long userId,
                                                    @RequestBody UserDTO dto, BindingResult bindingResult) {
        userValidator.validate(dto, bindingResult);
//        if (bindingResult.hasErrors()) {
//            LoggerUtil.getCurrentLogger().warning(accessor.getMessage("empty"));
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  //      }
        try {
            dto.setId(userId);
            User user = userService.update(dto);

            UserDTO userDTO = map(user);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "User rent book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book rented",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "User not found, Book out of stock",
                    content = @Content) })
    @RequestMapping(value = "/users/addBook/{userID}/{bookID}", method = RequestMethod.PUT)
    public ResponseEntity<?> userAddBook(@PathVariable("userID") Long userID,
                                               @PathVariable ("bookID") Long bookID) {

        try {
            userService.addBook(userID, bookID);
            User user = userService.getUserById(userID);
            UserDTO userDTO = mapWithRentals(user);

            return ResponseEntity.ok(userDTO);
        } catch (EntityNotFoundException | EntityAlreadyExistsException | OutOfStockException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "User return book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book returned",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "User not found",
                    content = @Content) })
    @RequestMapping(value = "/users/deleteBook/{userID}/{bookID}", method = RequestMethod.DELETE)
    public ResponseEntity<UserDTO> userDeleteBook(@PathVariable("userID") Long userID,
                                               @PathVariable ("bookID") Long bookID) {
        User user;
        try {
            userService.removeBook(userID, bookID);
            user = userService.getUserById(userID);
            UserDTO userDTO = mapWithRentals(user);

            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            LoggerUtil.getCurrentLogger().warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get Users by a Book they have rented")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users by Book id",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "",
                    content = @Content) })
    @RequestMapping(value = "users/getByBookId/{bookID}", method = RequestMethod.GET)
    public ResponseEntity<List<User>> getUsersByBookId(@PathVariable("bookID") Long bookID) {
        List<User> users = userService.getUsersByBookId(bookID);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    //for testing security purposes.
    @RequestMapping(value = "/users/auth", method = RequestMethod.GET)
    public ResponseEntity<SecurityContext> getAuth() {
        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return new ResponseEntity(HttpStatus.OK);
    }



    private UserDTO map(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setLastname(user.getLastname());
        dto.setFirstname(user.getFirstname());
        return dto;
    }

    private UserDTO mapWithRentals(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setEmail(user.getEmail());

        List<BookDTO> books = new ArrayList<>();
        for (Rental rental : user.getRentals()) {
            books.add(mapBook(rental.getBook()));
        }

        dto.setBooksRented(books);

        return dto;
    }

    private BookDTO mapBook(Book book) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle(book.getTitle());
        bookDTO.setAuthorLastname(book.getAuthor().getLastname());
        bookDTO.setAuthorFirstname(book.getAuthor().getFirstname());
        bookDTO.setId(book.getId());
        bookDTO.setNumberOfCopies(book.getNumberOfCopies());

        return bookDTO;
    }
}
