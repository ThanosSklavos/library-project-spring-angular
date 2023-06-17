package gr.aueb.cf.libraryapp.repository;

import gr.aueb.cf.libraryapp.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findBookByTitleStartingWith(String title);
    Book findBookByTitle(String title);
    Book findBookById(Long id);

}
