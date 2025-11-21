package com.antiquelife.antiquelifebackend;

import com.antiquelife.antiquelifebackend.Category;
import com.antiquelife.antiquelifebackend.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // Ця анотація Lombok сама створить конструктор для ін'єкції репозиторію
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категорію з ID " + id + " не знайдено"));
    }
}