package com.antiquelife.antiquelifebackend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucketName;

    private final RestTemplate restTemplate;

    // Впроваджуємо RestTemplate через конструктор
    public ImageService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String uploadImage(MultipartFile file) throws IOException {
        // 1. Генеруємо унікальне ім'я файлу
        // Наприклад: a1b2c3d4-image.jpg
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString() + "-" + originalFilename;

        // 2. Формуємо URL для завантаження (API Supabase)
        // POST /storage/v1/object/{bucket}/{filename}
        String uploadUrl = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucketName, fileName);

        // 3. Налаштовуємо заголовки запиту
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.setContentType(MediaType.valueOf(file.getContentType()));

        // 4. Створюємо тіло запиту (байти картинки)
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

        // 5. Відправляємо файл у Supabase
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            // 6. Якщо успішно — формуємо ПУБЛІЧНЕ посилання
            // Формат: {supabaseUrl}/storage/v1/object/public/{bucket}/{filename}
            if (response.getStatusCode() == HttpStatus.OK) {
                return String.format("%s/storage/v1/object/public/%s/%s", supabaseUrl, bucketName, fileName);
            } else {
                throw new RuntimeException("Помилка завантаження: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Не вдалося з'єднатися з Supabase: " + e.getMessage());
        }
    }
}