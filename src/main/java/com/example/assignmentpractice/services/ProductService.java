package com.example.assignmentpractice.services;

import com.example.assignmentpractice.models.Product;
import com.example.assignmentpractice.repo.CategoryRepo;
import com.example.assignmentpractice.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("ps")
public class ProductService implements IProductService{
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private CategoryRepo categoryRepo;

    @Override
    public List<Product> getAllProducts(){

        System.out.println("Getting all products from DB");
        return productRepo.findAll();
    }
    @Override
    public Product getProductById(Long id){
        Optional<Product> product = productRepo.findById(id);
        if(product.isPresent()){
            return product.get();
        }else{
            throw new IllegalArgumentException("Product id " + id + " does not exist");
        }
    }
    @Override
    public Product createProduct(Product product){
        return productRepo.save(product);
    }
}
