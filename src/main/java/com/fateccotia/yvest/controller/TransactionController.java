package com.fateccotia.yvest.controller;

import com.fateccotia.yvest.entity.Transaction;
import com.fateccotia.yvest.enums.TransactionStatus;
import com.fateccotia.yvest.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions(@RequestHeader("token") String token) {
        try {
            List<Transaction> transactions = transactionService.findAllByUser(token);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Integer id, @RequestHeader("token") String token) {
        try {
            Optional<Transaction> transaction = transactionService.findById(id, token);
            return transaction.map(ResponseEntity::ok)
                             .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction, @RequestHeader("token") String token) {
        try {
            Transaction savedTransaction = transactionService.save(transaction, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Integer id, @RequestBody Transaction transactionDetails, @RequestHeader("token") String token) {
        try {
            Transaction updatedTransaction = transactionService.update(id, transactionDetails, token);
            if (updatedTransaction != null) {
                return ResponseEntity.ok(updatedTransaction);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Integer id, @RequestHeader("token") String token) {
        try {
            boolean deleted = transactionService.delete(id, token);
            if (deleted) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Transaction>> getTransactionsByStatus(@PathVariable TransactionStatus status, @RequestHeader("token") String token) {
        try {
            List<Transaction> transactions = transactionService.findByStatus(token, status);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Transaction>> getTransactionsByCategory(@PathVariable Integer categoryId, @RequestHeader("token") String token) {
        try {
            List<Transaction> transactions = transactionService.findByCategory(token, categoryId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Transaction>> getTransactionsByDateRange(@RequestParam LocalDate startDate, 
                                                                        @RequestParam LocalDate endDate, 
                                                                        @RequestHeader("token") String token) {
        try {
            List<Transaction> transactions = transactionService.findByDateRange(token, startDate, endDate);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<Map<String, Double>> getBalance(@RequestHeader("token") String token) {
        try {
            Double balance = transactionService.getBalance(token);
            return ResponseEntity.ok(Map.of("balance", balance));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/totals")
    public ResponseEntity<Map<String, Double>> getTotals(@RequestHeader("token") String token) {
        try {
            Double income = transactionService.getTotalByStatus(token, TransactionStatus.INCOME);
            Double expense = transactionService.getTotalByStatus(token, TransactionStatus.EXPENSE);
            Double balance = income - expense;
            
            return ResponseEntity.ok(Map.of(
                "income", income,
                "expense", expense,
                "balance", balance
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}