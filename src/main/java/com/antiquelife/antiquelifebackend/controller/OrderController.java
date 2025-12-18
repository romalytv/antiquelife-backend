package com.antiquelife.antiquelifebackend.controller;

import com.antiquelife.antiquelifebackend.dto.OrderRequest;
import com.antiquelife.antiquelifebackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173") // Дозволяємо доступ з Vue (перевірте свій порт)
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request) {
        UUID orderId = orderService.placeOrder(request);
        return ResponseEntity.ok("Замовлення створено з ID: " + orderId);
    }
}
