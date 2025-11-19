package com.fateccotia.yvest.service;

import com.fateccotia.yvest.entity.Transaction;
import com.fateccotia.yvest.entity.User;
import com.fateccotia.yvest.enums.TransactionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FinanceChatService {

    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private AuthService authService;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, String> responseCache = new ConcurrentHashMap<>();

    public String askFinancialQuestion(String question, String token) {
        User user = authService.toUser(token);
        if (user == null) {
            return "🔐 Usuário não autenticado. Faça login novamente.";
        }
        
        // Verificar cache
        String cacheKey = user.getId() + ":" + question.toLowerCase().trim();
        if (responseCache.containsKey(cacheKey)) {
            return responseCache.get(cacheKey);
        }
        
        try {
            // Obter contexto financeiro do usuário
            String financialContext = getFinancialContext(user);
            
            // Construir e enviar prompt para Ollama
            String response = callOllama(question, financialContext);
            
            // Armazenar no cache (expira após 10 minutos)
            responseCache.put(cacheKey, response);
            
            return response;
            
        } catch (Exception e) {
            return "❌ Desculpe, estou com problemas técnicos. Tente novamente em alguns instantes.";
        }
    }
    
    private String getFinancialContext(User user) {
        StringBuilder context = new StringBuilder();
        
        try {
            // Obter totais
            Double totalIncome = transactionService.getTotalByStatus(getUserToken(user), TransactionStatus.INCOME);
            Double totalExpense = transactionService.getTotalByStatus(getUserToken(user), TransactionStatus.EXPENSE);
            Double balance = totalIncome - totalExpense;
            
            // Obter transações recentes (últimos 30 dias)
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(30);
            List<Transaction> recentTransactions = transactionService.findByDateRange(getUserToken(user), startDate, endDate);
            
            // Construir contexto formatado
            context.append("=== RESUMO FINANCEIRO ===\n");
            context.append("💰 Saldo Atual: R$ ").append(String.format("%.2f", balance)).append("\n");
            context.append("📈 Total Receitas: R$ ").append(String.format("%.2f", totalIncome)).append("\n");
            context.append("📉 Total Despesas: R$ ").append(String.format("%.2f", totalExpense)).append("\n\n");
            
            context.append("=== ÚLTIMAS TRANSAÇÕES (30 dias) ===\n");
            if (recentTransactions.isEmpty()) {
                context.append("Nenhuma transação recente.\n");
            } else {
                int count = 0;
                for (Transaction transaction : recentTransactions) {
                    if (count >= 8) break; // Limitar a 8 transações
                    String emoji = transaction.getStatus() == TransactionStatus.INCOME ? "🟢" : "🔴";
                    String type = transaction.getStatus() == TransactionStatus.INCOME ? "RECEITA" : "DESPESA";
                    context.append(emoji).append(" ")
                          .append(transaction.getDate().format(DateTimeFormatter.ofPattern("dd/MM")))
                          .append(" | ").append(type)
                          .append(" | R$ ").append(String.format("%.2f", transaction.getAmount()))
                          .append(" | ").append(transaction.getDescription())
                          .append("\n");
                    count++;
                }
            }
            
        } catch (Exception e) {
            context.append("⚠️ Dados financeiros limitados disponíveis.\n");
        }
        
        return context.toString();
    }
    
    private String callOllama(String question, String financialContext) {
        String ollamaUrl = "http://localhost:11434/api/generate";
        
        try {
            // Montar request para Ollama
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "phi3:mini");
            requestBody.put("prompt", buildFinancialPrompt(question, financialContext));
            requestBody.put("stream", false);
            
            // Otimizações para respostas financeiras
            Map<String, Object> options = new HashMap<>();
            options.put("temperature", 0.1);
            options.put("top_p", 0.9);
            options.put("num_predict", 300);
            options.put("repeat_penalty", 1.1);
            requestBody.put("options", options);
            
            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // Fazer requisição com timeout
            ResponseEntity<String> response = restTemplate.postForEntity(ollamaUrl, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Parse da resposta JSON
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                String responseText = jsonResponse.get("response").asText();
                return formatFinancialResponse(responseText);
            } else {
                return "📊 Com base nos seus dados financeiros, recomendo acompanhar seus gastos regularmente.";
            }
            
        } catch (Exception e) {
            // Fallback para quando Ollama não está disponível
            return getFallbackResponse(question, financialContext);
        }
    }
    
    private String buildFinancialPrompt(String question, String financialContext) {
        return String.format("""
            Você é um consultor financeiro especializado em finanças pessoais. 
            Analise os dados financeiros abaixo e responda a pergunta do usuário de forma PRÁTICA, ÚTIL e ESPECÍFICA.

            DADOS FINANCEIROS DO CLIENTE:
            %s

            PERGUNTA DO CLIENTE: %s

            INSTRUÇÕES PARA RESPOSTA:
            - Responda em PORTUGUÊS do Brasil
            - Seja DIRETO e PRÁTICO
            - Use EMOJIs para tornar visual (💰, 📈, 📉, ⚠️, 💡, 🎯)
            - Formate com tópicos usando • 
            - Baseie-se APENAS nos dados fornecidos
            - Dê conselhos ESPECÍFICOS baseados nos números
            - Mencione valores concretos dos dados quando possível
            - Seja ENCORAJADOR e REALISTA
            - Máximo 150 palavras

            FORMATO DA RESPOSTA:
            💰 [Resumo inicial]
            • [Ponto 1 específico]
            • [Ponto 2 específico] 
            • [Ponto 3 específico]
            🎯 [Recomendação principal]

            RESPOSTA:
            """, financialContext, question);
    }
    
    private String formatFinancialResponse(String response) {
        // Limpar e formatar a resposta
        response = response.trim();
        
        // Garantir que comece com emoji
        if (!response.startsWith("💰") && !response.startsWith("📈") && 
            !response.startsWith("⚠️") && !response.startsWith("💡")) {
            response = "💡 " + response;
        }
        
        return response;
    }
    
    private String getFallbackResponse(String question, String financialContext) {
        // Resposta fallback quando Ollama não está disponível
        if (question.toLowerCase().contains("economizar") || question.toLowerCase().contains("gastar menos")) {
            return "💰 Para economizar mais:\n• Acompanhe todos os gastos diários\n• Estabeleça metas realistas\n• Revise assinaturas não essenciais\n🎯 Foque em reduzir 15% dos gastos variáveis";
        } else if (question.toLowerCase().contains("investir")) {
            return "📈 Para investir:\n• Crie uma reserva de emergência primeiro\n• Considere Tesouro Direto para começar\n• Diversifique conforme seu perfil\n🎯 Invista 20% da sua renda líquida";
        } else {
            return "💡 Com base na sua situação financeira, recomendo:\n• Acompanhar gastos regularmente\n• Estabelecer metas claras\n• Criar reserva para imprevistos\n🎯 Revise suas finanças semanalmente";
        }
    }
    
    public String getFinancialAnalysis(String token) {
        User user = authService.toUser(token);
        if (user == null) {
            return "🔐 Usuário não autenticado.";
        }
        
        String financialContext = getFinancialContext(user);
        
        String analysisPrompt = """
            Faça uma ANÁLISE FINANCEIRA COMPLETA baseada nos dados abaixo.
            Inclua:

            1. 📊 DIAGNÓSTICO DA SITUAÇÃO
            - Situação atual (positiva/negativa/estável)
            - Pontos fortes e fracos

            2. ⚠️ ALERTAS IMPORTANTES
            - Possíveis problemas
            - Áreas de atenção

            3. 💡 RECOMENDAÇÕES PRÁTICAS
            - 3 ações específicas para melhorar
            - Prazos realistas

            4. 🎯 METAS SUGERIDAS
            - Meta para próximo mês
            - Meta para 3 meses

            Dados: %s

            Análise:
            """.formatted(financialContext);
        
        try {
            return callOllama(analysisPrompt, financialContext);
        } catch (Exception e) {
            return "📊 Sua análise financeira mostra que é importante:\n• Manter controle mensal de gastos\n• Diversificar fontes de renda\n• Criar reserva para emergências\n🎯 Estabeleça metas claras e mensuráveis";
        }
    }
    
    public String getSpendingTips(String token) {
        User user = authService.toUser(token);
        if (user == null) {
            return "🔐 Usuário não autenticado.";
        }
        
        String tipsPrompt = """
            Baseado nos padrões de gastos, forneça 5 DICAS PRÁTICAS E ESPECÍFICAS para economizar dinheiro.
            Seja CONCRETO e sugira ações REALISTAS que possam ser implementadas imediatamente.

            Dados: %s

            Dicas Práticas:
            """.formatted(getFinancialContext(user));
        
        try {
            return callOllama(tipsPrompt, "");
        } catch (Exception e) {
            return "💡 5 Dicas para Economizar:\n• 📝 Registre todos os gastos diários\n• 🛒 Faça lista de compras antes de ir ao mercado\n• 💳 Evite compras por impulso - espere 24h\n• 🏠 Reduza desperdícios de energia e água\n• 🎯 Estabeleça meta de economia semanal";
        }
    }
    
    public String getInvestmentAdvice(String token) {
        User user = authService.toUser(token);
        if (user == null) {
            return "🔐 Usuário não autenticado.";
        }
        
        String investmentPrompt = """
            Baseado na situação financeira, forneça CONSELHOS DE INVESTIMENTO apropriados.
            Considere:
            - Perfil do investidor (conservador/moderado)
            - Valor disponível para investir
            - Objetivos financeiros
            - Prazo dos investimentos

            Dados: %s

            Conselhos de Investimento:
            """.formatted(getFinancialContext(user));
        
        try {
            return callOllama(investmentPrompt, "");
        } catch (Exception e) {
            return "📈 Conselhos de Investimento:\n• 🏦 Reserve 3-6 meses de gastos para emergências\n• 📊 Comece com Tesouro Direto (Selic ou IPCA)\n• ⏱️ Defina prazos (curto, médio e longo)\n• 🔄 Diversifique entre diferentes aplicações\n• 🎯 Reinvesta os rendimentos regularmente";
        }
    }
    
    private String getUserToken(User user) {
        try {
            return authService.getValidUserToken(user);
        } catch (Exception e) {
            return "temp-token-" + user.getId();
        }
    }
    
    public void clearCache() {
        responseCache.clear();
    }
    
    public Map<String, String> getServiceStatus() {
        Map<String, String> status = new HashMap<>();
        try {
            // Testar conexão com Ollama
            restTemplate.getForEntity("http://localhost:11434/api/tags", String.class);
            status.put("ollama", "✅ Conectado");
            status.put("cache_size", String.valueOf(responseCache.size()));
            status.put("status", "✅ Serviço operacional");
        } catch (Exception e) {
            status.put("ollama", "❌ Desconectado");
            status.put("status", "⚠️ Serviço com limitações");
        }
        return status;
    }
}