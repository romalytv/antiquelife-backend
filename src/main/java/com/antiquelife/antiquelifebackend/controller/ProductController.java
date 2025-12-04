package com.antiquelife.antiquelifebackend.controller;

import com.antiquelife.antiquelifebackend.dto.ProductRequest;
import com.antiquelife.antiquelifebackend.entity.Product;
import com.antiquelife.antiquelifebackend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<Product> getAll() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getOne(@PathVariable UUID id) {
        return productService.getProductById(id);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Product> create(
            @RequestPart("product") ProductRequest request,
            @RequestPart("images") List<MultipartFile> images
    ) {
        try {
            Product createdProduct = productService.createProduct(request, images);
            return ResponseEntity.ok(createdProduct);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}