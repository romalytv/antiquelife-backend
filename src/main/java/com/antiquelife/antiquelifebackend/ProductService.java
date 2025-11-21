package com.antiquelife.antiquelifebackend;

import com.antiquelife.antiquelifebackend.dto.ProductRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService; // Наш сервіс для Supabase

    // 1. Метод створення товару
    @Transactional // Якщо щось піде не так, зміни в БД відкотяться
    public Product createProduct(ProductRequest request, MultipartFile imageFile) throws IOException {

        // Крок 1: Знаходимо категорію
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Категорію не знайдено!"));

        // Крок 2: Завантажуємо фото в Supabase
        String imageUrl = imageService.uploadImage(imageFile);

        // Крок 3: Створюємо і заповнюємо товар
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStatus(request.getStatus()); // наприклад "active"
        product.setEpoch(request.getEpoch());
        product.setOrigin(request.getOrigin());

        product.setCategory(category); // Встановлюємо зв'язок
        product.setImage_path(imageUrl); // Встановлюємо посилання на фото

        // Крок 4: Зберігаємо
        return productRepository.save(product);
    }

    // 2. Отримати всі товари
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 3. Отримати один товар
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар не знайдено"));
    }

    // 4. Видалити товар
    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }
}
