package com.antiquelife.antiquelifebackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Каже Spring, що це REST контролер
@RequestMapping("/api/customers") // Всі запити будуть починатися з /api/customers
@CrossOrigin(origins = "*") // Дозволяє запити з будь-якого фронтенду (важливо для розробки)
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Цей метод слухає POST запити. Саме сюди прилетять дані з форми.
    @PostMapping("/add")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        // @RequestBody перетворює JSON, який прийшов з фронтенду, в об'єкт Java
        Customer savedCustomer = customerService.saveCustomer(customer);

        // Повертаємо збереженого клієнта і статус 201 (Created)
        return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
    }
}