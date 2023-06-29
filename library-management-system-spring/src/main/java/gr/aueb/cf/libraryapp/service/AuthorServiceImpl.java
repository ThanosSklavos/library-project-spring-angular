package gr.aueb.cf.libraryapp.service;

import gr.aueb.cf.libraryapp.model.Author;
import gr.aueb.cf.libraryapp.dto.AuthorDTO;
import gr.aueb.cf.libraryapp.repository.AuthorRepository;
import gr.aueb.cf.libraryapp.service.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.libraryapp.service.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements IAuthorService{

    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional
    @Override
    public Author insertAuthor(AuthorDTO authorDTO) throws EntityAlreadyExistsException {
        if (authorRepository.findAuthorByLastname(authorDTO.getLastname()) != null) {
            throw new EntityAlreadyExistsException(Author.class);
        }
        return authorRepository.save(map(authorDTO));
    }

    @Transactional
    @Override
    public Author deleteAuthor(Long id) throws EntityNotFoundException {
        Author author = authorRepository.findAuthorById(id);
        if (author == null) {
            throw new EntityNotFoundException(Author.class, id);
        }
        authorRepository.deleteById(author.getId());
        return author;
    }

    @Transactional
    @Override
    public List<Author> getAuthorByLastname(String lastname) throws EntityNotFoundException {
        List<Author> authors;
        authors = authorRepository.findByLastnameStartingWith(lastname);
        if (authors.size() == 0) {
            throw new EntityNotFoundException(Author.class, 0L);
        }
        return authors;
    }

    @Transactional
    @Override
    public Author getAuthorById(Long id) throws EntityNotFoundException {
        Optional<Author> author;

        author = authorRepository.findById(id);
        if (author.isEmpty()) {
            throw new EntityNotFoundException(Author.class, id);
        }
        return author.get();
    }

    private Author map(AuthorDTO dto) {
        Author author = new Author();
        author.setId(dto.getId());
        author.setLastname(dto.getLastname());
        author.setFirstname(dto.getFirstname());
        return author;
    }
}
