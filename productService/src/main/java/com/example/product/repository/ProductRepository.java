package com.example.product.repository;

import com.example.product.domain.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Product entity.
 */
@Repository
public interface ProductRepository extends MongoRepository<Product, String> {}
