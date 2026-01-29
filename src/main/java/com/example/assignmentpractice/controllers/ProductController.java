package com.example.assignmentpractice.controllers;

import com.example.assignmentpractice.dtos.CategoryDto;
import com.example.assignmentpractice.dtos.ProductDto;
import com.example.assignmentpractice.models.Category;
import com.example.assignmentpractice.models.Product;
import com.example.assignmentpractice.services.IProductService;
import com.example.assignmentpractice.services.ProductService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    @Qualifier("fkps")
    private IProductService productService;

    @GetMapping
    public List<ProductDto> getAllProducts() {
        List<ProductDto> response = new ArrayList<>();
        List<Product> products = productService.getAllProducts();
        for(Product product : products) {
            response.add(from(product));
        }
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        if (id == 0) {
            throw new IllegalArgumentException("Please pass id > 0");
        } else if(id < 0) {
            throw new IllegalArgumentException("Invalid Id");
        }

        Product product = productService.getProductById(id);
        if (product != null) {
            ProductDto resp = from(product);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } else {
            throw new IllegalArgumentException("Product with requested id not found");
        }
    }

    @PostMapping
    public ProductDto createProduct(@RequestBody ProductDto productDto) {

        Product product = from(productDto);
        Product response = productService.createProduct(product);
        return from(response);

    }

    public ProductDto from(Product product){
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setPrice(product.getPrice());
        productDto.setImageUrl(product.getImageUrl());
        productDto.setDescription(product.getDescription());
        if(product.getCategory() != null){
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setId(product.getCategory().getId());
            categoryDto.setName(product.getCategory().getName());
            categoryDto.setDescription(product.getCategory().getDescription());
            productDto.setCategory(categoryDto);
        }
        return productDto;
    }
    public Product from(ProductDto productDto){
        Product product = new Product();
        product.setId(productDto.getId());
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setImageUrl(productDto.getImageUrl());
        product.setDescription(productDto.getDescription());
        if(productDto.getCategory() != null){
            Category category = new Category();
            category.setId(productDto.getCategory().getId());
            category.setName(productDto.getCategory().getName());
            category.setDescription(productDto.getCategory().getDescription());
            product.setCategory(category);
        }
        return product;
    }
}
