package gr.aueb.cf.libraryapp.service;

import gr.aueb.cf.libraryapp.dto.LoginDTO;
import gr.aueb.cf.libraryapp.dto.UserDTO;
import gr.aueb.cf.libraryapp.model.Book;
import gr.aueb.cf.libraryapp.model.User;
import gr.aueb.cf.libraryapp.service.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.libraryapp.model.Rental;
import gr.aueb.cf.libraryapp.repository.BookRepository;
import gr.aueb.cf.libraryapp.repository.RentalRepository;
import gr.aueb.cf.libraryapp.repository.UserRepository;
import gr.aueb.cf.libraryapp.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.libraryapp.service.exceptions.OutOfStockException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final RentalRepository rentalRepository;
    private final IBookService bookService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BookRepository bookRepository,
                           RentalRepository rentalRepository, IBookService bookService) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.rentalRepository = rentalRepository;
        this.bookService = bookService;
    }


    @Override
    public User insert(UserDTO dto) throws EntityAlreadyExistsException {
        if (userRepository.findUserByUsername(dto.getUsername()) != null) {
            throw new EntityAlreadyExistsException(User.class);
        }
        return userRepository.save(map(dto));
    }

    @Override
    public User update(UserDTO dto) throws EntityNotFoundException {
        User user = map(dto);
        if (userRepository.findUserById(user.getId()) == null) {    // check by id if the entity we want to update exists
            throw new EntityNotFoundException(User.class, user.getId());
        }
        return userRepository.save(user);
    }

    @Override
    public User delete(Long id) throws EntityNotFoundException {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new EntityNotFoundException(User.class, id);
        }
        userRepository.deleteById(user.getId());
        return user;
    }

    @Override
    public List<User> getUsersByUsername(String username) throws EntityNotFoundException {
        List<User> users;
        users = userRepository.findUserByUsernameStartingWith(username);
        if (users.size() == 0) {
            throw new EntityNotFoundException(User.class, 0L);
        }
        return users;
    }

    @Override
    public List<User> getAllUsers() throws EntityNotFoundException {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new EntityNotFoundException(User.class);
        }
        return users;
    }

    @Override
    public User getUserById(Long id) throws EntityNotFoundException {
        Optional<User> user;

        user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new EntityNotFoundException(User.class, id);
        }
        return user.get();
    }

    @Override
    public LoginDTO isUserValid(LoginDTO loginDTO) {
        User user = userRepository.findUserByUsername(loginDTO.getUsername().trim());
        if (user == null) {
            return loginDTO; //with id null
        }
        String hashedPassword = user.getPassword().trim();

        if (BCrypt.checkpw(loginDTO.getPassword(), hashedPassword)) {
            loginDTO.setPassword(null);
            loginDTO.setLoggedInUserId(user.getId());
        }
        return loginDTO;
    }

    @Override
    public void addBook(Long userID, Long bookID) throws EntityAlreadyExistsException, EntityNotFoundException, OutOfStockException {
        User user = userRepository.findUserById(userID);
        Book book = bookRepository.findBookById(bookID);


        if (user == null || book == null) {
            throw new EntityNotFoundException(User.class, userID);
        }

        if (rentalRepository.findByUserIdAndBookId(userID, bookID) != null) {
            throw new EntityAlreadyExistsException(Rental.class);
        }

        bookService.decreaseNumberOfCopies(book);
        bookRepository.save(book);

        Rental rental = new Rental();
        rental.setUser(user);
        rental.setBook(book);
        rental.setRentalDate(LocalDate.now());

        // Update relationships on both sides
        user.getRentals().add(rental);
        book.getRentals().add(rental);

        rentalRepository.save(rental);

    }

    @Override
    public void removeBook(Long userID, Long bookID) throws EntityNotFoundException {
        User user = userRepository.findUserById(userID);
        Book book = bookRepository.findBookById(bookID);

        if (user == null || book == null) {
            throw new EntityNotFoundException(User.class, userID);
        }

        Rental rental = rentalRepository.findByUserIdAndBookId(userID, bookID);

        if (rental != null) {
            bookService.increaseNumberOfCopies(book);
            bookRepository.save(book);

            user.getRentals().remove(rental);
            book.getRentals().remove(rental);

            userRepository.save(user);
            bookRepository.save(book);
            rentalRepository.delete(rental);
        } else {
            throw new EntityNotFoundException(Rental.class);
        }
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepository.findUserByUsername(username) != null;
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.findUserByEmail(email) != null;
    }

    private User map(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setLastname(dto.getLastname());
        user.setFirstname(dto.getFirstname());
        user.setEmail(dto.getEmail());
        user.setPassword(hashPassword(dto.getPassword()));
        return user;
    }

    private String hashPassword(String password) {
        int workload = 12;
        String salt = BCrypt.gensalt(workload);
        return BCrypt.hashpw(password, salt);
    }
}
