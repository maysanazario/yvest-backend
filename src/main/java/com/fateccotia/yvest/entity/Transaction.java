package com.fateccotia.yvest.entity;

import com.fateccotia.yvest.enums.TransactionStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;

@Entity
public class Transaction {
    // Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    
    private Double amount;
    
    private LocalDate date;
    
    private String description;
    
    @Enumerated(EnumType.ORDINAL)
    private TransactionStatus status;
    
    @ManyToOne
    private Category category;
    
    @ManyToOne
    private User user;

    // Constructors
    public Transaction() { }

    public Transaction(Integer id, Double amount, LocalDate date, String description, 
                      TransactionStatus status, Category category, User user) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.status = status;
        this.category = category;
        this.user = user;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}