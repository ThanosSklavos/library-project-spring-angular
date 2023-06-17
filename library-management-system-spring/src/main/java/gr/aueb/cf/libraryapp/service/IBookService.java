package gr.aueb.cf.libraryapp.service;

import gr.aueb.cf.libraryapp.dto.BookDTO;
import gr.aueb.cf.libraryapp.model.Author;
import gr.aueb.cf.libraryapp.model.Book;
import gr.aueb.cf.libraryapp.service.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.libraryapp.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.libraryapp.service.exceptions.OutOfStockException;
import java.util.List;

public interface IBookService {

    /**
     * Inserts a new {@link Book} into the database based on the data carried by the {@link BookDTO}.
     * If the book's {@link Author} does not exist in the database, a new Author is created.
     *
     * @param bookDTO The DTO object that contains the data of the book to be inserted.
     * @return The inserted {@link Book} instance.
     * @throws EntityAlreadyExistsException If a book with the same data already exists in the database.
     */
    Book insertBook(BookDTO bookDTO) throws EntityAlreadyExistsException;

    /**
     * Removes a {@link Book} from the database based on the provided ID.
     *
     * @param id The ID of the book to be removed.
     * @return The removed {@link Book} instance.
     * @throws EntityNotFoundException If no book with the provided ID is found in the database.
     */
    Book removeBook(Long id) throws EntityNotFoundException;

    /**
     * Retrieves a list of {@link Book} from the database that match the provided title.
     *
     * @param title The title or a part of the title to search for.
     * @return A list of {@link Book} instances that match the search criteria.
     * @throws EntityNotFoundException If no books matching the provided title are found in the database.
     */
    List<Book> getBooksByTitle(String title) throws EntityNotFoundException;

    /**
     * Retrieves a {@link Book} from the database based on the provided ID.
     *
     * @param id The ID of the book to retrieve.
     * @return The {@link Book} instance with the provided ID.
     * @throws EntityNotFoundException If no book with the provided ID is found in the database.
     */
    Book getBookById(Long id) throws EntityNotFoundException;

    /**
     * Updates the details of a {@link Book} in the database based on the data carried by the {@link BookDTO}.
     *
     * @param bookDTO The DTO object that contains the updated data of the book.
     * @return The updated {@link Book} instance.
     * @throws EntityNotFoundException If no book with the provided ID is found in the database.
     */
    Book updateBook(BookDTO bookDTO) throws EntityNotFoundException;

    /**
     * Decreases the number of copies available for a {@link Book} in the database.
     *
     * @param book The {@link Book} instance for which the number of copies should be decreased.
     * @return The updated {@link Book} instance with the decreased number of copies.
     * @throws OutOfStockException If the book becomes out of stock after decreasing the number of copies.
     */
    Book decreaseNumberOfCopies(Book book) throws OutOfStockException;

    /**
     * Increases the number of copies available for a {@link Book} in the database.
     *
     * @param book The {@link Book} instance for which the number of copies should be increased.
     * @return The updated {@link Book} instance with the increased number of copies.
     */
    Book increaseNumberOfCopies(Book book);

    /**
     * Retrieves a list of all {@link Book} available in the database.
     *
     * @return A list of all {@link Book} instances in the library.
     * @throws EntityNotFoundException If no books are found in the database.
     */
    List<Book> getAllBooks() throws EntityNotFoundException;

}
