package ir.repository;

import ir.entity.Category;
import ir.entity.Expert;
import ir.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpertRepository extends JpaRepository<Expert, Long>, JpaSpecificationExecutor<Expert> {
    Optional<Expert> findByUserName(String username);

    Optional<Expert> findByEmailAndPassword(String email, String password);
}
