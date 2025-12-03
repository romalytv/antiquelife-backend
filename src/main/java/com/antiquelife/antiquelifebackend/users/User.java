package com.antiquelife.antiquelifebackend.users;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID user_id;

    private String username;
    private String password;
    private String role; // Тут буде лежати "ROLE_ADMIN"
}