package com.fateccotia.yvest.repository;

import com.fateccotia.yvest.entity.Category;
import com.fateccotia.yvest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByUser(User user);
    List<Category> findByUserId(Integer userId);
    
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.category_name = :categoryName AND c.user = :user")
    boolean existsByCategoryNameAndUser(@Param("categoryName") String categoryName, @Param("user") User user);
}