package gr.aueb.cf.libraryapp.service;

import gr.aueb.cf.libraryapp.dto.LoginDTO;
import gr.aueb.cf.libraryapp.dto.UserDTO;
import gr.aueb.cf.libraryapp.model.Book;
import gr.aueb.cf.libraryapp.model.User;
import gr.aueb.cf.libraryapp.service.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.libraryapp.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.libraryapp.service.exceptions.OutOfStockException;
import java.util.List;

public interface IUserService {

    /**
     * Inserts a new {@link User} into the database with hashed password
     * based on the data carried by the {@link UserDTO}.
     *
     * @param dto The DTO object that contains the data of the user to be inserted.
     * @return The inserted {@link User} instance.
     * @throws EntityAlreadyExistsException If a user with the same data already exists in the database.
     */
    User insert(UserDTO dto) throws EntityAlreadyExistsException;

    /**
     * Updates the details of a {@link User} in the database based on the data carried by the {@link UserDTO}.
     *
     * @param dto The DTO object that contains the updated data of the user.
     * @return The updated {@link User} instance.
     * @throws EntityNotFoundException If no user with the provided ID is found in the database.
     */
    User update(UserDTO dto) throws EntityNotFoundException;

    /**
     * Deletes a {@link User} from the database based on the provided ID.
     *
     * @param id The ID of the user to be deleted.
     * @return The removed {@link User} instance.
     * @throws EntityNotFoundException If no user with the provided ID is found in the database.
     */
    User delete(Long id) throws EntityNotFoundException;

    /**
     * Retrieves a list of {@link User} from the database that match the provided username.
     *
     * @param username The username or a part of the username to search for.
     * @return A list of {@link User} instances that match the search criteria.
     * @throws EntityNotFoundException If no users matching the provided username are found in the database.
     */
    List<User> getUsersByUsername(String username) throws EntityNotFoundException;

    /**
     * Retrieves a list of all {@link User} available in the database.
     *
     * @return A list of all {@link User} instances in the database.
     * @throws EntityNotFoundException If no users are found in the database.
     */
    List<User> getAllUsers() throws EntityNotFoundException;

    /**
     * Retrieves a {@link User} from the database based on the provided ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The {@link User} instance with the provided ID.
     * @throws EntityNotFoundException If no user with the provided ID is found in the database.
     */
    User getUserById(Long id) throws EntityNotFoundException;

    /**
     * Validates the provided login credentials by checking if the username and password are correct
     *
     * @param loginDTO The DTO object that contains the login credentials.
     * @return The {@link LoginDTO} instance with the validation result.
     */
    LoginDTO isUserValid(LoginDTO loginDTO);

    /**
     * Adds a {@link Book} to the user's rented books based on the provided user ID and book ID.
     *
     * @param userID The ID of the user.
     * @param bookID The ID of the book to be added.
     * @throws EntityAlreadyExistsException If the user already has the book borrowed.
     * @throws EntityNotFoundException If the user or book with the provided IDs is not found in the database.
     * @throws OutOfStockException If the book is out of stock.
     */
    void addBook(Long userID, Long bookID) throws EntityAlreadyExistsException, EntityNotFoundException, OutOfStockException;

    /**
     * Removes a {@link Book} from the user's borrowed books based on the provided user ID and book ID.
     *
     * @param userID The ID of the user.
     * @param bookID The ID of the book to be removed.
     * @throws EntityNotFoundException If the user or book with the provided IDs is not found in the database.
     */
    void removeBook(Long userID, Long bookID) throws EntityNotFoundException;

    /**
     * Checks if a username already exists in the database.
     *
     * @param username The username to check.
     * @return {@code true} if the username exists, {@code false} otherwise.
     */
    boolean usernameExists(String username);

    /**
     * Checks if an email already exists in the database.
     *
     * @param email The email to check.
     * @return {@code true} if the email exists, {@code false} otherwise.
     */
    boolean emailExists(String email);
}
