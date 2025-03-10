package ir.repository;

import ir.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> findByFirstNameAndLastName(String firstName, String lastName);

    Optional<List<Customer>> findAllByFirstName(String firstName);

    Optional<List<Customer>> findAllByLastName(String lastName);

    @Modifying
    @Query("""
            update Customer c set c.firstName=: firstName where c.id =:id
            """)
    void updateCustomerFirstName(Long id, String firstName);


}
