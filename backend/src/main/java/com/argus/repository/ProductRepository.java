package com.argus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.argus.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByUrl(String url);

    List<Product> findAllByUserId(Long userId);

    Optional<Product> findByIdAndUserId(Long id, Long userId);
}