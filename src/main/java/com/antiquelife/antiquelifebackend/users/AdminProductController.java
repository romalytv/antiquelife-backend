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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/admin/products") // Базовий шлях для всіх методів
public class AdminProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;
    private final ObjectMapper objectMapper; // Для перетворення рядка JSON в об'єкт

    @Autowired
    public AdminProductController(ProductRepository productRepository, CategoryRepository categoryRepository,  ImageService imageService, ObjectMapper objectMapper) {
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
            @RequestPart("product") String productJson, // JSON прийде як рядок
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        try {
            // 1. Ручний парсинг JSON (це найнадійніший спосіб при роботі з файлами)
            ProductRequest request = objectMapper.readValue(productJson, ProductRequest.class);

            // 2. Якщо є файл — вантажимо через твій сервіс
            String imageUrl = null;
            if (image != null && !image.isEmpty()) {
                imageUrl = imageService.uploadImage(image);
            }

            // 3. Зберігаємо в БД
            return saveProductToDb(new Product(), request, imageUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Помилка створення: " + e.getMessage());
        }
    }

    // --- UPDATE ---
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateProduct(
            @PathVariable UUID id,
            @RequestPart("product") String productJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        try {
            Product existingProduct = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Товар не знайдено"));

            ProductRequest request = objectMapper.readValue(productJson, ProductRequest.class);

            // Логіка оновлення фото:
            // Якщо прислали нове фото -> вантажимо і оновлюємо URL
            // Якщо фото null -> залишаємо старе посилання
            String imageUrl = existingProduct.getImage_path();
            if (image != null && !image.isEmpty()) {
                imageUrl = imageService.uploadImage(image);
            }

            return saveProductToDb(existingProduct, request, imageUrl);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Помилка оновлення: " + e.getMessage());
        }
    }

    // --- Helper Method ---
    private ResponseEntity<?> saveProductToDb(Product product, ProductRequest request, String imageUrl) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Категорію не знайдено"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStatus(request.getStatus());
        product.setEpoch(request.getEpoch());
        product.setOrigin(request.getOrigin());
        product.setCategory(category);

        // Оновлюємо картинку тільки якщо вона змінилась (не null)
        if (imageUrl != null) {
            product.setImage_path(imageUrl);
        }

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
