package gr.aueb.cf.libraryapp.service;

import gr.aueb.cf.libraryapp.dto.BookDTO;
import gr.aueb.cf.libraryapp.model.Author;
import gr.aueb.cf.libraryapp.model.Book;
import gr.aueb.cf.libraryapp.repository.AuthorRepository;
import gr.aueb.cf.libraryapp.repository.BookRepository;
import gr.aueb.cf.libraryapp.service.exceptions.EntityAlreadyExistsException;
import gr.aueb.cf.libraryapp.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.libraryapp.service.exceptions.OutOfStockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements IBookService{

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @Override
    public Book insertBook(BookDTO bookDTO) throws EntityAlreadyExistsException {
        if (bookRepository.findBookByTitle(bookDTO.getTitle()) != null) {
            throw new EntityAlreadyExistsException(Book.class);
        }

        if (authorRepository.findAuthorByLastname(bookDTO.getAuthorLastname()) == null) {
            Author author = new Author();
            author.setLastname(bookDTO.getAuthorLastname());
            author.setFirstname(bookDTO.getAuthorFirstname());
            authorRepository.save(author);
            bookDTO.setAuthorLastname(author.getLastname());
        }

        return bookRepository.save(map(bookDTO));
    }

    @Override
    public Book removeBook(Long id) throws EntityNotFoundException {
        Book book = bookRepository.findBookById(id);
        if (book == null) {
            throw new EntityNotFoundException(Book.class, id);
        }
        bookRepository.delete(book);
        return book;
    }

    @Override
    public List<Book> getBooksByTitle(String title) throws EntityNotFoundException {
        List<Book> books;
        books = bookRepository.findBookByTitleStartingWith(title);
        if (books.size() == 0) {
            throw new EntityNotFoundException(Book.class, 0L);
        }
        return books;
    }

    @Override
    public Book getBookById(Long id) throws EntityNotFoundException {
        Optional<Book> book;

        book = bookRepository.findById(id);
        if (book.isEmpty()) {
            throw new EntityNotFoundException(Book.class, id);
        }
        return book.get();
    }

    @Override
    public Book updateBook(BookDTO bookDTO) throws EntityNotFoundException {
        Book book = map(bookDTO);
        if (bookRepository.findBookById(book.getId()) == null) {
            throw new EntityNotFoundException(Book.class, book.getId());
        }
        return  bookRepository.save(book);
    }

    @Override
    public Book decreaseNumberOfCopies(Book book) throws OutOfStockException {
        int numberOfCopies = book.getNumberOfCopies();

        if (numberOfCopies > 0) {
            book.setNumberOfCopies(--numberOfCopies);
        } else {
            throw new OutOfStockException(book);
        }
        return book;
    }

    @Override
    public Book increaseNumberOfCopies(Book book) {
        book.setNumberOfCopies(book.getNumberOfCopies() + 1);
        return book;
    }

    @Override
    public List<Book> getAllBooks() throws EntityNotFoundException {
        List<Book> books = bookRepository.findAll();
        System.out.println(books);
        if (books.isEmpty()) {
            throw new EntityNotFoundException(Book.class);
        }
        return books;
    }

    private Book map (BookDTO dto) {
        Book book = new Book();
        book.setId(dto.getId());
        book.setTitle(dto.getTitle());
        book.setNumberOfCopies(dto.getNumberOfCopies());

        Author author = authorRepository.findAuthorByLastname(dto.getAuthorLastname());
        book.setAuthor(author);
        return book;
    }
}
