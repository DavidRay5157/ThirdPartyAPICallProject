package com.example.assignmentpractice.config;

import com.example.assignmentpractice.models.Category;
import com.example.assignmentpractice.models.Product;
import com.example.assignmentpractice.repo.CategoryRepo;
import com.example.assignmentpractice.repo.ProductRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final CategoryRepo categoryRepository;
    private final ProductRepo productRepository;

    public DataLoader(CategoryRepo categoryRepository, ProductRepo productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Optional: Only seed if the DB is empty
        if (categoryRepository.count() == 0) {

            // 1. Initialize Categories (No IDs needed!)
            Category electronics = new Category();
            electronics.setName("Electronics");
            electronics.setDescription("Latest tech and gadgets");

            Category homeOffice = new Category();
            homeOffice.setName("Home & Office");
            homeOffice.setDescription("Furniture and stationery");

            // Save categories first to generate their IDs
            categoryRepository.saveAll(List.of(electronics, homeOffice));

            // 2. Initialize Products and link to saved Categories
            Product laptop = new Product();
            laptop.setName("MacBook Pro");
            laptop.setDescription("M3 Chip, 16GB RAM");
            laptop.setPrice(1999.99);
            laptop.setCategory(electronics); // Link the object

            Product chair = new Product();
            chair.setName("Ergonomic Chair");
            chair.setDescription("Lumbar support for long hours");
            chair.setPrice(250.00);
            chair.setCategory(homeOffice);

            productRepository.saveAll(List.of(laptop, chair));

            System.out.println(">> Database seeded with sample categories and products.");
        } else {
            System.out.println(">> Database already contains data, skipping seed.");
        }
    }
}
