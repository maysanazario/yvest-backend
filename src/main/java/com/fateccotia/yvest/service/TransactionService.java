package com.fateccotia.yvest.service;

import com.fateccotia.yvest.entity.Transaction;
import com.fateccotia.yvest.entity.User;
import com.fateccotia.yvest.enums.TransactionStatus;
import com.fateccotia.yvest.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AuthService authService;

    public List<Transaction> findAllByUser(String token) {
        User user = authService.toUser(token);
        if (user == null) {
            throw new RuntimeException("Usuário não autenticado");
        }
        return transactionRepository.findByUser(user);
    }

    public Optional<Transaction> findById(Integer id, String token) {
        User user = authService.toUser(token);
        if (user == null) {
            throw new RuntimeException("Usuário não autenticado");
        }
        
        Optional<Transaction> transaction = transactionRepository.findById(id);
        
        if (transaction.isPresent() && transaction.get().getUser().getId().equals(user.getId())) {
            return transaction;
        }
        return Optional.empty();
    }

    public Transaction save(Transaction transaction, String token) {
        User user = authService.toUser(token);
        if (user == null) {
            throw new RuntimeException("Usuário não autenticado");
        }
        transaction.setUser(user);
        return transactionRepository.save(transaction);
    }

    public Transaction update(Integer id, Transaction transactionDetails, String token) {
        User user = authService.toUser(token);
        if (user == null) {
            throw new RuntimeException("Usuário não autenticado");
        }
        
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        
        if (optionalTransaction.isPresent() && optionalTransaction.get().getUser().getId().equals(user.getId())) {
            Transaction transaction = optionalTransaction.get();
            transaction.setAmount(transactionDetails.getAmount());
            transaction.setDate(transactionDetails.getDate());
            transaction.setDescription(transactionDetails.getDescription());
            transaction.setStatus(transactionDetails.getStatus());
            transaction.setCategory(transactionDetails.getCategory());
            
            return transactionRepository.save(transaction);
        }
        return null;
    }

    public boolean delete(Integer id, String token) {
        User user = authService.toUser(token);
        if (user == null) {
            throw new RuntimeException("Usuário não autenticado");
        }
        
        Optional<Transaction> transaction = transactionRepository.findById(id);
        
        if (transaction.isPresent() && transaction.get().getUser().getId().equals(user.getId())) {
            transactionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Transaction> findByStatus(String token, TransactionStatus status) {
        User user = authService.toUser(token);
        if (user == null) {
            throw new RuntimeException("Usuário não autenticado");
        }
        return transactionRepository.findByUserAndStatus(user, status);
    }

    public List<Transaction> findByCategory(String token, Integer categoryId) {
        User user = authService.toUser(token);
        if (user == null) {
            throw new RuntimeException("Usuário não autenticado");
        }
        return transactionRepository.findByUserAndCategoryId(user, categoryId);
    }

    public List<Transaction> findByDateRange(String token, LocalDate startDate, LocalDate endDate) {
        User user = authService.toUser(token);
        if (user == null) {
            throw new RuntimeException("Usuário não autenticado");
        }
        return transactionRepository.findByUserAndDateBetween(user, startDate, endDate);
    }

    public Double getTotalByStatus(String token, TransactionStatus status) {
        User user = authService.toUser(token);
        if (user == null) {
            throw new RuntimeException("Usuário não autenticado");
        }
        Double total = transactionRepository.getTotalAmountByUserAndStatus(user, status);
        return total != null ? total : 0.0;
    }

    public Double getBalance(String token) {
        User user = authService.toUser(token);
        if (user == null) {
            throw new RuntimeException("Usuário não autenticado");
        }
        Double income = getTotalByStatus(token, TransactionStatus.INCOME);
        Double expense = getTotalByStatus(token, TransactionStatus.EXPENSE);
        return income - expense;
    }
    
    // Método adicional para obter o usuário do token (se necessário em outros lugares)
    public User getUserFromToken(String token) {
        return authService.toUser(token);
    }
}