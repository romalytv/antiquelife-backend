package com.antiquelife.antiquelifebackend.users;

import com.antiquelife.antiquelifebackend.dto.ProductRequest;
import com.antiquelife.antiquelifebackend.entity.Category;
import com.antiquelife.antiquelifebackend.entity.Product;
import com.antiquelife.antiquelifebackend.repo.CategoryRepository;
import com.antiquelife.antiquelifebackend.repo.ProductRepository;
import com.antiquelife.antiquelifebackend.service.ImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;
    private final ObjectMapper objectMapper;

    @Autowired
    public AdminProductController(ProductRepository productRepository, CategoryRepository categoryRepository, ImageService imageService, ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.imageService = imageService;
        this.objectMapper = objectMapper;
    }

    // 1. READ ALL (Отримати список всіх товарів для таблиці)
    // GET: /admin/products
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 2. READ ONE (Отримати один товар по ID, щоб заповнити форму редагування)
    // GET: /admin/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID id) {
        Optional<Product> product = productRepository.findById(id);

        // Якщо товар є - повертаємо його, якщо немає - 404 Not Found
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> createProduct(
            @RequestPart("product") String productJson,
            // 👇 ЗМІНА 1: Приймаємо список файлів
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        try {
            ProductRequest request = objectMapper.readValue(productJson, ProductRequest.class);
            Product product = new Product();

            // Логіка збору картинок
            List<String> finalImages = new ArrayList<>();

            // 1. Завантажуємо нові (якщо є)
            if (images != null) {
                for (MultipartFile file : images) {
                    String url = imageService.uploadImage(file);
                    finalImages.add(url);
                }
            }

            // Валідація кількості (1-10)
            if (finalImages.isEmpty()) return ResponseEntity.badRequest().body("Має бути хоча б 1 фото");
            if (finalImages.size() > 10) return ResponseEntity.badRequest().body("Максимум 10 фото");

            return saveProductToDb(product, request, finalImages);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Помилка створення: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateProduct(
            @PathVariable UUID id,
            @RequestPart("product") String productJson,
            // 👇 ЗМІНА 2: Приймаємо список нових файлів
            @RequestPart(value = "images", required = false) List<MultipartFile> newImages
    ) {
        try {
            Product existingProduct = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Товар не знайдено"));

            ProductRequest request = objectMapper.readValue(productJson, ProductRequest.class);

            // Логіка збору картинок: СТАРІ (з JSON) + НОВІ (з файлів)
            List<String> finalImages = new ArrayList<>();

            // 1. Додаємо старі, які юзер залишив (вони приходять в JSON)
            if (request.getImageUrls() != null) {
                finalImages.addAll(request.getImageUrls());
            }

            // 2. Додаємо нові завантажені
            if (newImages != null) {
                for (MultipartFile file : newImages) {
                    String url = imageService.uploadImage(file);
                    finalImages.add(url);
                }
            }

            // Валідація
            if (finalImages.isEmpty()) return ResponseEntity.badRequest().body("Має залишитись хоча б 1 фото");
            if (finalImages.size() > 10) return ResponseEntity.badRequest().body("Максимум 10 фото");

            return saveProductToDb(existingProduct, request, finalImages);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Помилка оновлення: " + e.getMessage());
        }
    }

    // --- Helper Method ---
    private ResponseEntity<?> saveProductToDb(Product product, ProductRequest request, List<String> imageUrls) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Категорію не знайдено"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStatus(request.getStatus());
        product.setEpoch(request.getEpoch());
        product.setOrigin(request.getOrigin());
        product.setCategory(category);
        product.setQuantity(request.getQuantity());
        product.setImageUrls(imageUrls);

        Product saved = productRepository.save(product);
        return ResponseEntity.ok(saved);
    }

    // 5. DELETE (Видалити товар)
    // DELETE: /admin/products/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable UUID id) {
        try {
            // Перевіряємо чи існує товар перед видаленням
            if (productRepository.existsById(id)) {
                productRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content (успішно, тіла немає)
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
