package gr.aueb.cf.libraryapp.repository;

import gr.aueb.cf.libraryapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findUserByUsernameStartingWith(String username);
    User findUserById(Long id);

    User findUserByUsername(String username);
    User findUserByEmail(String email);
}
