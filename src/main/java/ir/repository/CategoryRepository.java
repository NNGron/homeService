package ir.repository;

import ir.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    Optional<List<Category>> findByName(String name);

    List<Category> findCategoriesByParentCategoryIsNull();

    List<Category> findCategoriesByParentCategoryIsNotNull();

    List<Category> findCategoriesByNameInAndParentCategoryIsNotNull(List<String> names);

    Optional<Category> findCategoryByNameAndParentCategoryIsNotNull(String name);

    Optional<Category> findCategoryByNameAndParentCategoryIsNull(String name);
}
