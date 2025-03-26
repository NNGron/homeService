package ir.repository.user;

import ir.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for performing CRUD operations and custom queries on {@link User} entities.
 * This interface extends {@link JpaRepository} to provide basic database operations and
 * {@link JpaSpecificationExecutor} for executing specification-based queries.
 *
 * <p>All methods in this repository are transactional by default, and this interface
 * serves as a bridge between the database and the application service layer.</p>
 *
 * <p>Annotations:</p>
 * <ul>
 *     <li>{@link Repository}: Indicates that this is a Spring Data Repository, making it eligible for
 *         Spring's exception translation mechanisms.</li>
 * </ul>
 *
 * <p>Custom Methods:</p>
 * <ul>
 *     <li>{@link #findByEmail(String)}: Retrieves a user by their email address.</li>
 *     <li>{@link #findByEmailAndPassword(String, String)}: Retrieves a user by their email address and password.</li>
 * </ul>
 *
 * @see JpaRepository
 * @see JpaSpecificationExecutor
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * Finds a {@link User} entity by its email address.
     *
     * @param email The email address of the user to find.
     * @return An {@link Optional} containing the user if found, or empty if no user exists with the given email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a {@link User} entity by its email address and password.
     *
     * @param email    The email address of the user to find.
     * @param password The password of the user to find.
     * @return An {@link Optional} containing the user if found, or empty if no user exists with the given email and password.
     */
    Optional<User> findByEmailAndPassword(String email, String password);

}
