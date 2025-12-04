package com.antiquelife.antiquelifebackend.controller;

import com.antiquelife.antiquelifebackend.dto.AIRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/admin/ai")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://antiquelife.onrender.com"
}, allowCredentials = "true")
public class AIController {

    @Value("${openai.api.key}")
    private String OPENAI_API_KEY;

    @Value("${openai.project.id}")
    private String OPENAI_PROJECT_ID;

    private final String OPENAI_URL = "https://api.openai.com/v1/responses";

    @PostMapping("/scan")
    public ResponseEntity<?> scanImage(@RequestBody AIRequest request) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(OPENAI_API_KEY);
        headers.add("OpenAI-Project", OPENAI_PROJECT_ID); // вставити свій

        List<Map<String, Object>> contentList = new ArrayList<>();

        // 1. Формуємо повідомлення
        String prompt = """
            Ти професійний оцінювач антикваріату та мистецтвознавець.
            Твоє завдання — максимально точно ідентифікувати предмет на фото.
            
            1. УВАЖНО ПРОЧИТАЙ будь-який текст, клейма (stamps), підписи на дні чи звороті предмету. Це найважливіше!
            2. Визнач: Бренд (Manufacturer), Модель (Pattern), Матеріал (напр. Фаянс, Порцеляна), Техніку (напр. Transferware), Період.
            3. Поверни результат ТІЛЬКИ у форматі JSON (без markdown) з такими полями:
            
            - "name": Сформуй коротку комерційну назву українською. Формат: "Тип + Назва Моделі + Бренд + Рік" (напр. "Глибока тарілка The Cottage, Lunéville, 1920-ті").
            - "epoch": Орієнтовний період (напр. "XIX ст." або "1920-1940 рр.").
            - "origin": Країна та місто походження (напр. "Франція, Люневіль").
            - "price": Твоя оцінка ринкової вартості в ГРИВНЯХ (лише число, без валюти).
            - "category_guess": Одне слово для категорії (напр. "Посуд", "Меблі", "Декор").
            - "description": Детальний, "продаючий" опис українською мовою. Структуруй його так:
               Спочатку напиши історію предмету та виробника.
               Потім опиши візуальний стиль (сцена, кольори).
               В кінці додай технічні характеристики списком з емодзі:
               ⚙️ Матеріал: ...
               🎨 Техніка: ...
               📏 Розмір: (напиши "приблизно Ø 20-25 см", бо ти не знаєш точно)
               💙 Стан: (оціни візуально)
            """;

// Формуємо контент повідомлення (спочатку текст)
        contentList.add(Map.of(
                "type", "input_text",
                "text", prompt
        ));

// Додаємо всі зображення
        if (request.getImages() != null) {
            for (String base64Image : request.getImages()) {

                // Якщо прилітає чистий base64 – додаємо data URI
                if (!base64Image.startsWith("data:")) {
                    base64Image = "data:image/jpeg;base64," + base64Image;
                }

                contentList.add(Map.of(
                        "type", "input_image",
                        "image_url", base64Image   // <-- А НЕ image, А НЕ {url: ...}
                ));
            }
        }

// user message
        Map<String, Object> userMsg = Map.of(
                "role", "user",
                "content", contentList
        );

// === НОВИЙ ПРАВИЛЬНИЙ PAYLOAD ===
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "gpt-5.1");
        payload.put("input", List.of(userMsg));   // <── ДУЖЕ ВАЖЛИВО
        payload.put("max_output_tokens", 1000);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_URL, entity, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error connecting to OpenAI: " + e.getMessage());
        }
    }

}