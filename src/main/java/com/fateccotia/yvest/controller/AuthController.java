package com.fateccotia.yvest.controller;

import com.fateccotia.yvest.entity.Token;
import com.fateccotia.yvest.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> user) {
        String username = user.get("username");
        String email = user.get("email");
        String password = user.get("password");
        
        try {
            authService.signup(username, email, password);
            return ResponseEntity.ok("Cadastrado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro no cadastro: " + e.getMessage());
        }
    }
    
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Map<String, String> user) {
        String email = user.get("email");
        String password = user.get("password");

        Token token = authService.signin(email, password);
        if (token != null) {
            return ResponseEntity.ok(token);
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    
    @PostMapping("/signout")
    public ResponseEntity<?> signout(@RequestHeader String token) {
        authService.signout(token);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("token") String authHeader) {
        try {
            // Extrair token do header
            String token = extractTokenFromHeader(authHeader);
            
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token não fornecido");
            }
            
            // Validar token
            Boolean isValid = authService.validate(token);
            
            if (Boolean.TRUE.equals(isValid)) {
                return ResponseEntity.ok("Token válido");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou expirado");
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro na validação");
        }
    }

    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && !authHeader.trim().isEmpty()) {
            return authHeader.trim(); // Retorna o token diretamente
        }
        return null;
    }
}