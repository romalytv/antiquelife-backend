package com.antiquelife.antiquelifebackend.dto;

import lombok.Data;

@Data
public class CustomerDTO {
    private String firstName;
    private String lastName;
    private String email;
    private Long phone;
    private String address;
    private String country;
}
