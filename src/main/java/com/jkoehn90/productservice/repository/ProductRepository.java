package com.jkoehn90.productservice.repository;

import com.jkoehn90.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByPriceLessThanEqual(Double price);

    List<Product> findByQuantityGreaterThan(Integer quantity);
}
