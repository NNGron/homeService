package ir.repository;

import ir.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long>, JpaSpecificationExecutor<Offer> {
    @Query("SELECT COUNT(o) FROM Offer o WHERE o.expert.email = :email")
    long countOffersByExpertEmail(@Param("email") String email);
}
