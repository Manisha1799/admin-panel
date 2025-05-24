package com.admin.service;

import com.admin.dto.ProductRequest;
import com.admin.model.Product;
import com.admin.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    
    public Product createProduct(ProductRequest request) {
        Product product = new Product();
        updateProductFromRequest(product, request);
        return productRepository.save(product);
    }

    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    
    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    
    public Product updateProduct(Long id, ProductRequest request) {
        Product product = getProductById(id);
        updateProductFromRequest(product, request);
        return productRepository.save(product);
    }

    
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private void updateProductFromRequest(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(request.getCategory());
    }
} 