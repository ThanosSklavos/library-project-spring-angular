package gr.aueb.cf.libraryapp.repository;

import gr.aueb.cf.libraryapp.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    Rental findByUserIdAndBookId(Long userId, Long bookId);

    List<Rental> findByBookId(Long bookId);

}
