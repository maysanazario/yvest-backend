package com.fateccotia.yvest.repository;

import com.fateccotia.yvest.entity.Transaction;
import com.fateccotia.yvest.entity.User;
import com.fateccotia.yvest.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    
    List<Transaction> findByUser(User user);
    
    List<Transaction> findByUserAndStatus(User user, TransactionStatus status);
    
    List<Transaction> findByUserAndCategoryId(User user, Integer categoryId);
    
    List<Transaction> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.user = :user AND t.date BETWEEN :startDate AND :endDate AND t.status = :status")
    List<Transaction> findByUserAndDateBetweenAndStatus(@Param("user") User user, 
                                                       @Param("startDate") LocalDate startDate, 
                                                       @Param("endDate") LocalDate endDate, 
                                                       @Param("status") TransactionStatus status);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user = :user AND t.status = :status")
    Double getTotalAmountByUserAndStatus(@Param("user") User user, 
                                        @Param("status") TransactionStatus status);
}