package com.shoppingbot.repository;

import com.shoppingbot.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(String category);

    List<Product> findByCategoryAndPriceLessThanEqual(String category, BigDecimal maxPrice);

    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.price <= :maxPrice ORDER BY p.price ASC")
    List<Product> findAffordableProducts(@Param("category") String category, @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByKeyword(@Param("keyword") String keyword);
}
