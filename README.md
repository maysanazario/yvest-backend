<div align="center">
  
  <img src="https://capsule-render.vercel.app/api?type=waving&color=0:1E88E5,100:0D47A1&height=200&section=header&text=Yvest&fontSize=80&fontAlignY=35&animation=fadeIn&fontColor=white" width="100%"/>
  
  ### **Gerenciador de Finanças Pessoais com IA**

  [![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
  [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=flat-square&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
  [![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?style=flat-square&logo=mysql&logoColor=white)](https://www.mysql.com/)
  [![Ollama](https://img.shields.io/badge/Ollama-phi3%3Amini-000000?style=flat-square&logo=ollama&logoColor=white)](https://ollama.com/)
  
  ![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-FFD700?style=flat-square)
  [![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)](LICENSE)

  > **Projeto Integrador — 2º Semestre**  
  > *FATEC Cotia — Desenvolvimento de Software Multiplataforma*

</div>

---

## ✨ Sobre o Projeto

O **Yvest** é uma API REST desenvolvida como projeto integrador do segundo semestre da FATEC Cotia. A aplicação oferece um sistema completo de gerenciamento financeiro pessoal, combinando **controle de transações e categorias** com um **assistente financeiro inteligente** baseado em IA local.

### 🎯 Diferenciais

- 🔐 **Autenticação segura** com tokens de sessão
- 📊 **Controle financeiro completo** (receitas/despesas)
- 🤖 **Assistente financeiro com IA** utilizando Ollama + phi3:mini
- 💡 **Análises personalizadas** baseadas nos dados reais do usuário
- ⚡ **Cache inteligente** para otimizar respostas da IA
- 🎨 **API RESTful** com endpoints bem estruturados

---

## 🏗️ Arquitetura do Sistema

```mermaid
graph LR
    A[Cliente] --> B[API Spring Boot]
    B --> C[(MySQL)]
    B --> D[Ollama + phi3:mini]
    D --> E[Cache de Respostas]
    C --> F[Usuários]
    C --> G[Categorias]
    C --> H[Transações]
    style B fill:#6DB33F,stroke:#333,stroke-width:2px
    style D fill:#000000,stroke:#333,stroke-width:2px
```

### 📊 Modelo de Dados

```mermaid
erDiagram
    User ||--o{ Category : possui
    User ||--o{ Transaction : realiza
    User ||--o{ Token : gera
    Category ||--o{ Transaction : categoriza
    
    User {
        int id PK
        string username
        string email
        string password
        datetime created_at
    }
    
    Category {
        int id PK
        string category_name
        int user_id FK
    }
    
    Transaction {
        int id PK
        decimal amount
        datetime date
        string description
        enum status
        int category_id FK
        int user_id FK
    }
    
    Token {
        int id PK
        int user_id FK
        string token
        datetime expiration
    }
```

---

## 🔌 Endpoints da API

### 🔐 Autenticação `/auth`

```http
POST   /auth/signup      # Cadastro de usuário
POST   /auth/signin      # Login → retorna token (1h)
POST   /auth/signout     # Logout → invalida token
GET    /auth/validate    # Validação de token
```

### 🏷️ Categorias `/categories`

```http
GET    /categories       # Lista categorias do usuário
POST   /categories       # Cria nova categoria
PUT    /categories/{id}  # Atualiza categoria
DELETE /categories/{id}  # Remove categoria
```

### 💸 Transações `/transactions`

```http
GET    /transactions                    # Lista todas
GET    /transactions/{id}               # Busca por ID
GET    /transactions/status/{status}    # Filtra por status
GET    /transactions/category/{id}      # Filtra por categoria
GET    /transactions/range?start=&end=  # Filtra por data
GET    /transactions/balance            # Saldo atual
POST   /transactions                    # Nova transação
PUT    /transactions/{id}               # Atualiza
DELETE /transactions/{id}               # Remove
```

### 🤖 Assistente IA `/chat`

```http
POST   /chat/ask         # Pergunta financeira
GET    /chat/analysis    # Análise financeira completa
GET    /chat/tips        # Dicas personalizadas
GET    /chat/investment  # Conselhos de investimento
GET    /chat/status      # Status do Ollama
DELETE /chat/cache       # Limpa cache
```

---

## 🚀 Tecnologias Utilizadas

<div align="center">

| Camada | Tecnologia | Finalidade |
|--------|------------|------------|
| **Linguagem** | Java 17+ | Base da aplicação |
| **Framework** | Spring Boot 3.x | API REST e injeção de dependências |
| **Persistência** | Spring Data JPA | ORM e queries |
| **Banco de Dados** | MySQL 8.x | Armazenamento relacional |
| **Segurança** | BCrypt + Tokens | Criptografia e autenticação |
| **IA** | Ollama + phi3:mini | Assistente financeiro local |
| **Build** | Maven | Gerenciamento de dependências |

</div>

---

## 📁 Estrutura do Projeto

```
yvest/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/fateccotia/yvest/
│       │       ├── YvestApplication.java
│       │       ├── controller/         # Endpoints REST
│       │       │   ├── AuthController.java
│       │       │   ├── CategoryController.java
│       │       │   ├── TransactionController.java
│       │       │   └── ChatController.java
│       │       ├── service/            # Regras de negócio
│       │       │   ├── AuthService.java
│       │       │   ├── CategoryService.java
│       │       │   ├── TransactionService.java
│       │       │   └── FinanceChatService.java
│       │       ├── repository/         # Acesso a dados
│       │       │   ├── UserRepository.java
│       │       │   ├── CategoryRepository.java
│       │       │   ├── TransactionRepository.java
│       │       │   └── TokenRepository.java
│       │       ├── entity/             # Entidades JPA
│       │       │   ├── User.java
│       │       │   ├── Category.java
│       │       │   ├── Transaction.java
│       │       │   └── Token.java
│       │       └── enums/              # Enumeradores
│       │           └── TransactionStatus.java
│       └── resources/
│           └── application.properties  # Configurações
└── pom.xml                             # Dependências
```

---

## ⚙️ Como Executar

### Pré-requisitos

```bash
# Verificar instalações necessárias
java --version          # Java 17+
mysql --version         # MySQL 8+
ollama --version        # Ollama instalado
```

### Passo a Passo

```bash
# 1. Clone o repositório
git clone https://github.com/seu-usuario/yvest.git
cd yvest

# 2. Configure o banco de dados
# Edite src/main/resources/application.properties:
# spring.datasource.url=jdbc:mysql://localhost:3306/yvest
# spring.datasource.username=seu_usuario
# spring.datasource.password=sua_senha

# 3. Baixe o modelo de IA
ollama pull phi3:mini

# 4. Execute a aplicação
./mvnw spring-boot:run    # Linux/Mac
mvnw.cmd spring-boot:run  # Windows
```

A API estará disponível em: `http://localhost:8080`

---

## 🎓 Contexto Acadêmico

| | |
|---|---|
| **Instituição** | FATEC Cotia |
| **Curso** | Desenvolvimento de Software Multiplataforma (DSM) |
| **Disciplina** | Projeto Integrador |
| **Semestre** | 2º Semestre |
| **Período** | 2025 |

### Objetivos de Aprendizagem

- Aplicar conceitos de **API RESTful** com Spring Boot
- Implementar **autenticação segura** e controle de sessão
- Desenvolver **integração com IA local** (Ollama)
- Utilizar **boas práticas** de versionamento com Git
- Documentar **arquitetura e endpoints** de forma profissional

---

## 📈 Próximos Passos

- [ ] Implementar testes unitários e de integração
- [ ] Adicionar documentação Swagger/OpenAPI
- [ ] Implementar relatórios financeiros (PDF/Excel)
- [ ] Melhorar prompts e contexto do assistente IA

---

## 👥 Equipe de Desenvolvimento

| Nome | Função |
|------|--------|
| [Ana Clara MAdeira de Gois] | Documentação e Teste |
| [Jennifer Gabriely Lopes dos Santos] | Desenvolvedor Front-end |
| [Martie Bello Silva] | Documentação e Teste |
| [Maysa Alexandre Nazario] | Desenvolvedor Back-end |
| [Victória Heloísa de Melo Teixeira] | Desenvolvedor Front-end |

---

<div align="center">
  <br>
  <img src="https://capsule-render.vercel.app/api?type=waving&color=0:1E88E5,100:0D47A1&height=100&section=footer"/>
  
  <i>Projeto Integrador — 2º Semestre de DSM</i>
</div>
