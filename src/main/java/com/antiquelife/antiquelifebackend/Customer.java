package com.antiquelife.antiquelifebackend;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID customer_id;

    @Column(nullable = false)
    private String first_name;

    @Column(nullable = false)
    private String last_name;

    @Email
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Long phone;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String address;

    public Customer() {
    }

    public Customer(String first_name, String last_name, String email, Long phone, String country, String address) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone = phone;
        this.country = country;
        this.address = address;
    }

}