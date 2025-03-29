package ir.repository;

import ir.entity.Category;
import ir.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Optional<List<Order>> findAllByCategoryIn(List<Category> categories);

    List<Order> findAllByCustomerEmail(String customerEmail);
}