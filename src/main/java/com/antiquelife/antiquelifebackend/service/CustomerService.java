package com.antiquelife.antiquelifebackend.service;

import com.antiquelife.antiquelifebackend.entity.Customer;
import com.antiquelife.antiquelifebackend.repo.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, CustomerRepository customerRepository1) {
        this.customerRepository = customerRepository1;
    }

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
}
