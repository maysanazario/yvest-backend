package com.fateccotia.yvest.service;

import com.fateccotia.yvest.entity.Category;
import com.fateccotia.yvest.entity.User;
import com.fateccotia.yvest.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category create(Category category) {
        // Verificar se já existe uma categoria com o mesmo nome para o usuário
        if (categoryRepository.existsByCategoryNameAndUser(category.getCategory_name(), category.getUser())) {
            throw new RuntimeException("Já existe uma categoria com este nome");
        }
        return categoryRepository.save(category);
    }

    public List<Category> retrieve(User user) {
        return categoryRepository.findByUser(user);
    }

    public Optional<Category> findById(Integer id) {
        return categoryRepository.findById(id);
    }

    public Category update(Category category) {
        // Verificar se a categoria pertence ao usuário
        Optional<Category> existingCategory = categoryRepository.findById(category.getId());
        if (existingCategory.isPresent() && 
            existingCategory.get().getUser().getId().equals(category.getUser().getId())) {
            return categoryRepository.save(category);
        }
        throw new RuntimeException("Categoria não encontrada ou não pertence ao usuário");
    }

    public boolean delete(Integer id, User user) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent() && category.get().getUser().getId().equals(user.getId())) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Category> findByUserId(Integer userId) {
        return categoryRepository.findByUserId(userId);
    }
}