package gr.aueb.cf.libraryapp.service;

import gr.aueb.cf.libraryapp.dto.AuthorDTO;
import gr.aueb.cf.libraryapp.model.Author;
import gr.aueb.cf.libraryapp.service.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.libraryapp.service.exceptions.EntityNotFoundException;
import java.util.List;

public interface IAuthorService {

    /**
     * Inserts a new {@link Author} into the database based on the data carried by the {@link AuthorDTO}.
     *
     * @param authorDTO The DTO object that contains the data of the author to be inserted.
     * @return The inserted {@link Author} instance.
     * @throws EntityAlreadyExistsException If an author with the same data already exists in the database.
     */
    Author insertAuthor(AuthorDTO authorDTO) throws EntityAlreadyExistsException;

    /**
     * Deletes an {@link Author} from the database based on the provided ID.
     *
     * @param id The ID of the author to be deleted.
     * @return The removed {@link Author} instance.
     * @throws EntityNotFoundException If no author with the provided ID is found in the database.
     */
    Author deleteAuthor(Long id) throws EntityNotFoundException;

    /**
     * Retrieves a list of {@link Author} from the database that match the provided lastname.
     *
     * @param lastname The lastname or a part of the lastname to search for.
     * @return A list of {@link Author} instances that match the search criteria.
     * @throws EntityNotFoundException If no authors matching the provided lastname are found in the database.
     */
    List<Author> getAuthorByLastname(String lastname) throws EntityNotFoundException;

    /**
     * Retrieves an {@link Author} from the database based on the provided ID.
     *
     * @param id The ID of the author to retrieve.
     * @return The {@link Author} instance with the provided ID.
     * @throws EntityNotFoundException If no author with the provided ID is found in the database.
     */
    Author getAuthorById(Long id) throws EntityNotFoundException;
}
