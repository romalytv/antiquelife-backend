package com.antiquelife.antiquelifebackend.users;

import com.antiquelife.antiquelifebackend.dto.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtCore jwtCore;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtCore jwtCore) {
        this.authenticationManager = authenticationManager;
        this.jwtCore = jwtCore;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // 1. Пробуємо увійти (Spring сам перевірить пароль через BCrypt)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // 2. Якщо все ок, кладемо юзера в контекст безпеки
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. Генеруємо токен
            String jwt = jwtCore.generateToken(authentication);

            // 4. Віддаємо токен клієнту (простим текстом або JSON)
            return ResponseEntity.ok(jwt);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Невірний логін або пароль");
        }
    }
}