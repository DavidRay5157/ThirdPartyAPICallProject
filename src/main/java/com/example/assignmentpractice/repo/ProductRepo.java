package com.example.assignmentpractice.repo;

import com.example.assignmentpractice.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product,Long> {
    Optional<Product> findById(Long id);

    List<Product> findAll();

    Product save(Product p);

    void deleteById(Long id);

    List<Product> findProductByPriceBetween(Double low,Double high);

    List<Product> findAllByOrderByPrice();

    @Query("SELECT p.name from products p where p.price = :price")
    String getMeNameOfProductWhosePriceIs(Double price);


}
