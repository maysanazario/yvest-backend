package com.fateccotia.yvest.controller;

import com.fateccotia.yvest.entity.Category;
import com.fateccotia.yvest.entity.User;
import com.fateccotia.yvest.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Category category, 
                                  @RequestAttribute("user") User user) {
        try {
            category.setUser(user);
            Category saved = categoryService.create(category);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro ao criar categoria: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Category>> retrieve(@RequestAttribute("user") User user) {
        List<Category> list = categoryService.retrieve(user);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Integer id, 
                                    @RequestAttribute("user") User user) {
        Optional<Category> category = categoryService.findById(id);
        
        if (category.isPresent()) {
            // Verificar se a categoria pertence ao usuário
            if (category.get().getUser().getId().equals(user.getId())) {
                return ResponseEntity.ok(category.get());
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado a esta categoria");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categoria não encontrada");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, 
                                  @RequestBody Category category,
                                  @RequestAttribute("user") User user) {
        try {
            // Garantir que o ID do path corresponde ao ID do objeto
            category.setId(id);
            category.setUser(user);
            
            Category updated = categoryService.update(category);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao atualizar categoria: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, 
                                  @RequestAttribute("user") User user) {
        boolean deleted = categoryService.delete(id, user);
        if (deleted) {
            return ResponseEntity.ok("Categoria deletada com sucesso");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Categoria não encontrada ou não pertence ao usuário");
        }
    }
}