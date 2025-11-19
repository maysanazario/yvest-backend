package com.fateccotia.yvest.controller;

import com.fateccotia.yvest.service.FinanceChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/finance-chat")
@CrossOrigin(origins = "*")
public class FinanceChatController {

    @Autowired
    private FinanceChatService financeChatService;

    public static class QuestionRequest {
        private String question;
        
        public QuestionRequest() {}
        
        public QuestionRequest(String question) {
            this.question = question;
        }
        
        public String getQuestion() {
            return question;
        }
        
        public void setQuestion(String question) {
            this.question = question;
        }
    }

    @PostMapping("/ask")
    public ResponseEntity<Map<String, Object>> askQuestion(
            @RequestBody QuestionRequest request, // Usando DTO em vez de Map
            @RequestHeader("token") String token) {
        
        if (request == null || request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "❌ Pergunta não pode estar vazia"));
        }
        
        String question = request.getQuestion();
        
        // Limitar tamanho da pergunta
        if (question.length() > 500) {
            question = question.substring(0, 500) + "...";
        }
        
        try {
            String answer = financeChatService.askFinancialQuestion(question, token);
            return ResponseEntity.ok(Map.of(
                "answer", answer,
                "question", question,
                "timestamp", System.currentTimeMillis()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "❌ Erro interno: " + e.getMessage()));
        }
    }

    @PostMapping("/ask-map")
    public ResponseEntity<Map<String, Object>> askQuestionMap(
            @RequestBody Map<String, String> request,
            @RequestHeader("token") String token) {
        
        try {
            String question = request.get("question");
            if (question == null || question.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "❌ Pergunta não pode estar vazia"));
            }
            
            // Limitar tamanho da pergunta
            if (question.length() > 500) {
                question = question.substring(0, 500) + "...";
            }
            
            String answer = financeChatService.askFinancialQuestion(question, token);
            return ResponseEntity.ok(Map.of(
                "answer", answer,
                "question", question,
                "timestamp", System.currentTimeMillis()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "❌ Erro interno: " + e.getMessage()));
        }
    }

    @GetMapping("/analysis")
    public ResponseEntity<Map<String, Object>> getAnalysis(@RequestHeader("token") String token) {
        try {
            String analysis = financeChatService.getFinancialAnalysis(token);
            return ResponseEntity.ok(Map.of(
                "analysis", analysis,
                "type", "financial_analysis",
                "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "❌ Erro ao gerar análise"));
        }
    }

    @GetMapping("/tips")
    public ResponseEntity<Map<String, Object>> getTips(@RequestHeader("token") String token) {
        try {
            String tips = financeChatService.getSpendingTips(token);
            return ResponseEntity.ok(Map.of(
                "tips", tips,
                "type", "spending_tips", 
                "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "❌ Erro ao obter dicas"));
        }
    }

    @GetMapping("/investment-advice")
    public ResponseEntity<Map<String, Object>> getInvestmentAdvice(@RequestHeader("token") String token) {
        try {
            String advice = financeChatService.getInvestmentAdvice(token);
            return ResponseEntity.ok(Map.of(
                "advice", advice,
                "type", "investment_advice",
                "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "❌ Erro ao obter conselhos de investimento"));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        try {
            Map<String, String> status = financeChatService.getServiceStatus();
            return ResponseEntity.ok(Map.of(
                "status", "online",
                "services", status,
                "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/clear-cache")
    public ResponseEntity<Map<String, String>> clearCache(@RequestHeader("token") String token) {
        try {
            financeChatService.clearCache();
            return ResponseEntity.ok(Map.of("message", "✅ Cache limpo com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "❌ Erro ao limpar cache"));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "✅ Online",
            "service", "Finance Chat API",
            "version", "1.0",
            "timestamp", String.valueOf(System.currentTimeMillis())
        ));
    }
}