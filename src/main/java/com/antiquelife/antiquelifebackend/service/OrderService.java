package com.antiquelife.antiquelifebackend.service;

import com.antiquelife.antiquelifebackend.dto.OrderItemDTO;
import com.antiquelife.antiquelifebackend.dto.OrderRequest;
import com.antiquelife.antiquelifebackend.entity.Customer;
import com.antiquelife.antiquelifebackend.entity.Order;
import com.antiquelife.antiquelifebackend.entity.OrderDetail;
import com.antiquelife.antiquelifebackend.entity.Product;
import com.antiquelife.antiquelifebackend.repo.CustomerRepository;
import com.antiquelife.antiquelifebackend.repo.OrderDetailRepository;
import com.antiquelife.antiquelifebackend.repo.OrderRepository;
import com.antiquelife.antiquelifebackend.repo.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor // Автоматично створить конструктор для ін'єкції репозиторіїв
public class OrderService {

    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final OrderDetailRepository orderDetailRepo;
    private final CustomerRepository customerRepo;

    @Transactional // Якщо станеться помилка, нічого не запишеться в БД
    public UUID placeOrder(OrderRequest request) {

        // 1. Обробка клієнта (шукаємо існуючого або створюємо нового)
        Customer customer = customerRepo.findByEmail(request.getCustomer().getEmail())
                .orElse(new Customer());

        customer.setFirst_name(request.getCustomer().getFirstName());
        customer.setLast_name(request.getCustomer().getLastName());
        customer.setEmail(request.getCustomer().getEmail());
        customer.setPhone(request.getCustomer().getPhone());
        customer.setAddress(request.getCustomer().getAddress());
        customer.setCountry(request.getCustomer().getCountry());

        customer = customerRepo.save(customer);

        // 2. Створення замовлення
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus("NEW");
        // totalAmount поки 0, порахуємо нижче
        order = orderRepo.save(order);

        double totalAmount = 0.0;

        // 3. Додавання товарів
        for (OrderItemDTO itemDto : request.getItems()) {
            Product product = productRepo.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Товар не знайдено: " + itemDto.getProductId()));

            if (!"AVAILABLE".equals(product.getStatus())) {
                throw new RuntimeException("Товар '" + product.getName() + "' вже недоступний для замовлення (Статус: " + product.getStatus() + ")");
            }

            if (product.getQuantity() < itemDto.getQuantity()) {
                throw new RuntimeException("Недостатньо товару на складі для " + product.getName());
            }

            double newQuantity = product.getQuantity() - itemDto.getQuantity();
            product.setQuantity(newQuantity);

            // Якщо товару більше не лишилося (був 1, купили 1) -> Ставимо RESERVED
            if (newQuantity <= 0) {
                product.setStatus("RESERVED");
            }

            // Зберігаємо оновлений товар в БД
            productRepo.save(product);

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(itemDto.getQuantity());

            // ВАЖЛИВО: Ціну беремо з бази (Product), а не з JSON фронтенду!
            detail.setPriceAtSale(product.getPrice());

            orderDetailRepo.save(detail);

            totalAmount += product.getPrice() * itemDto.getQuantity();
        }

        // 4. Фінальне оновлення суми
        order.setTotalAmount(totalAmount);
        orderRepo.save(order);

        return order.getId();
    }
}