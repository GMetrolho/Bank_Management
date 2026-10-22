# Sistema de Gestão Bancária | Bank Management System

<div align="center">

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white)
![JDBC](https://img.shields.io/badge/JDBC-007396?style=for-the-badge&logo=java&logoColor=white)
![XML](https://img.shields.io/badge/XML-005FAD?style=for-the-badge&logo=xml&logoColor=white)

**Projeto Académico de Linguagens de Programação**

[Português](#português) • [English](#english)

</div>

---

## Português

### 📋 Descrição do Projeto

Este projeto académico consiste no desenvolvimento de um **Sistema de Gestão Bancária** que simula operações bancárias reais, implementado em Java. O sistema foi desenvolvido no âmbito da unidade curricular de **Linguagens de Programação** do curso de **Gestão de Sistemas e Computação**.

O sistema permite a gestão completa de clientes e contas bancárias, suportando operações fundamentais como depósitos, levantamentos, transferências e consulta de saldos, com persistência de dados em base de dados relacional SQLite e auditoria através de registos estruturados em XML.

### 🎯 Objetivos Académicos

- Aplicar conceitos avançados de **Programação Orientada a Objetos** (POO) em Java
- Implementar padrões de design como **DAO (Data Access Object)** para separação de responsabilidades
- Desenvolver um sistema de gestão transacional com suporte a operações atómicas (commit/rollback)
- Integrar persistência de dados com SQLite utilizando JDBC
- Implementar processamento concorrente com threads em Java
- Criar um sistema de auditoria robusto com registo estruturado em XML
- Garantir validação rigorosa de dados e tratamento de exceções

### ✨ Funcionalidades Principais

#### 1. Gestão de Clientes
- Registo de novos clientes com validação completa de dados
- Validação de NIF (Número de Identificação Fiscal) português
- Validação de idade mínima para abertura de conta
- Associação de clientes a contas bancárias

#### 2. Gestão de Contas Bancárias
- Suporte a dois tipos de conta:
  - **Conta Corrente**: Permite descoberto até um limite configurável
  - **Conta Poupança**: Rendimento de juros aplicado periodicamente
- Operações bancárias fundamentais:
  - Depósitos
  - Levantamentos
  - Transferências entre contas
  - Consulta de saldo e extrato

#### 3. Sistema Transacional
- Transações atómicas com suporte a **commit** e **rollback**
- Garantia de consistência de dados através de controlo transacional
- Isolamento de transações por thread utilizando **ThreadLocal**

#### 4. Persistência de Dados
- Base de dados relacional SQLite
- Padrão DAO para abstração do acesso a dados
- Gestão automática de conexões e recursos
- Controlo de integridade referencial

#### 5. Auditoria e Logging
- Sistema de logging estruturado em XML (LogManager)
- Registo de todas as operações críticas do sistema
- Timestamp e identificação de utilizador em cada operação
- Facilita auditoria e rastreabilidade de operações

#### 6. Processamento em Background
- Thread de monitorização (MonitorContas) executa continuamente
- Aplicação automática de juros a contas poupança a cada 90 segundos
- Operações executadas em background sem bloquear interface principal

#### 7. Interface de Utilizador
- Interface de linha de comando (CLI) intuitiva
- Menus interativos para navegação
- Validação de inputs em tempo real
- Mensagens de erro descritivas e úteis

### 🏗️ Arquitetura e Design

```
Banco/
├── src/                           # Código fonte
│   ├── DAO/                       # Camada de Acesso a Dados
│   │   ├── ClienteDAO.java        # Operações de persistência de clientes
│   │   ├── ContaDAO.java          # Operações de persistência de contas
│   │   ├── GestorBD.java          # Gestor central de base de dados
│   │   └── TransacaoDAO.java      # Operações de persistência de transações
│   ├── Interfaces/                # Interfaces do Sistema
│   │   └── Operacoes.java         # Interface de operações bancárias
│   ├── Main/                      # Ponto de Entrada
│   │   └── Main.java              # Classe principal e interface CLI
│   ├── models/                    # Modelos de Domínio
│   │   ├── Cliente.java           # Modelo de cliente
│   │   ├── Conta.java             # Classe abstrata de conta
│   │   ├── ContaCorrente.java     # Implementação de conta corrente
│   │   ├── ContaPoupanca.java     # Implementação de conta poupança
│   │   ├── Pessoa.java            # Classe abstrata base
│   │   ├── SaldoInsuficienteException.java  # Exceção customizada
│   │   └── Transacao.java         # Modelo de transação
│   └── services/                  # Camada de Serviços
│       ├── LogManager.java        # Gestão de logs XML
│       └── MonitorContas.java     # Thread de monitorização
├── bin/                           # Ficheiros compilados (.class)
├── lib/                           # Bibliotecas externas (JDBC SQLite)
├── banco.db                       # Base de dados SQLite (criada automaticamente)
├── logs.xml                       # Logs do sistema (criado automaticamente)
└── README.md                      # Este ficheiro
```

O sistema segue uma arquitetura em camadas com clara separação de responsabilidades:

#### Camada de Modelos (`models`)
- **Pessoa**: Classe abstrata representando indivíduos
- **Cliente**: Extensão de Pessoa com dados bancários
- **Conta**: Classe abstrata para contas bancárias
- **ContaCorrente** e **ContaPoupanca**: Implementações específicas de conta
- **Transacao**: Registo de operações bancárias
- **SaldoInsuficienteException**: Exceção customizada para controlo de saldo

#### Camada de Acesso a Dados (`DAO`)
- **GestorBD**: Gestor central de base de dados e conexões
- **ClienteDAO**: Operações de persistência de clientes
- **ContaDAO**: Operações de persistência de contas
- **TransacaoDAO**: Operações de persistência de transações

#### Camada de Interfaces (`Interfaces`)
- **Operacoes**: Interface que define operações bancárias base

#### Camada de Serviços (`services`)
- **LogManager**: Gestão de logs em formato XML
- **MonitorContas**: Thread de monitorização e aplicação de juros

#### Camada Principal (`Main`)
- **Main**: Ponto de entrada do sistema e interface CLI

### 🔧 Tecnologias e Ferramentas

| Tecnologia | Utilização | Versão |
|------------|-----------|--------|
| **Java** | Linguagem de programação principal | 8+ |
| **SQLite** | Base de dados relacional | 3.x |
| **JDBC** | API de conectividade com base de dados | - |
| **XML DOM** | Processamento e manipulação de XML | - |
| **Java Threads** | Programação concorrente | Java Threading API |
| **ThreadLocal** | Isolamento de conexões por thread | Java Concurrency |
| **Javadoc** | Documentação de código | - |

### 💾 Base de Dados e Persistência

O sistema utiliza SQLite como sistema de gestão de base de dados relacional, garantindo:

- **Atomicidade**: Transações são executadas completamente ou revertidas
- **Consistência**: Validações garantem integridade dos dados
- **Isolamento**: ThreadLocal assegura conexões isoladas por thread
- **Durabilidade**: Dados persistidos em disco

#### Esquema de Base de Dados

- **Tabela Clientes**: Armazena informação de clientes (NIF, nome, idade)
- **Tabela Contas**: Armazena contas bancárias (número, tipo, saldo, titular)
- **Tabela Transacoes**: Registo histórico de todas as operações

O padrão DAO abstrai completamente o acesso à base de dados, facilitando manutenção e possíveis migrações futuras.

### ⚙️ Concorrência e Processamento em Background

#### ThreadLocal para Isolamento
- Cada thread mantém sua própria conexão à base de dados
- Previne conflitos em ambientes multi-thread
- Garante isolamento transacional

#### MonitorContas
- Thread daemon executa em background
- Verifica contas poupança periodicamente (90 segundos)
- Aplica taxa de juro configurável automaticamente
- Não interfere com operações do utilizador

### 📝 Logging e Auditoria em XML

O sistema implementa um robusto sistema de auditoria através do **LogManager**:

- Logs estruturados em formato XML para fácil parsing
- Registo de eventos críticos:
  - Criação de clientes e contas
  - Operações bancárias (depósitos, levantamentos, transferências)
  - Erros e exceções
- Cada entrada contém:
  - Timestamp preciso
  - Tipo de operação
  - Detalhes da operação
  - Resultado (sucesso/falha)

### 🚀 Como Executar o Projeto

#### Pré-requisitos
- Java Development Kit (JDK) 8 ou superior instalado
- Driver JDBC para SQLite (incluído em `lib/`)

#### Passos de Execução

1. **Compilar o projeto**
   ```bash
   javac -d bin -cp "lib/*" src/**/*.java
   ```

2. **Executar a aplicação**
   ```bash
   java -cp "bin;lib/*" Main.Main
   ```

   No Linux/macOS, utilize `:` em vez de `;`:
   ```bash
   java -cp "bin:lib/*" Main.Main
   ```

3. **Interagir com o sistema**
   - Siga os menus apresentados na interface CLI
   - Selecione operações através de números
   - Insira dados quando solicitado



### 📚 Conceitos de POO Aplicados

- **Abstração**: Classes abstratas Pessoa e Conta
- **Encapsulamento**: Modificadores de acesso e getters/setters
- **Herança**: ContaCorrente e ContaPoupanca estendem Conta
- **Polimorfismo**: Implementação de interface Operacoes
- **Exceções Customizadas**: SaldoInsuficienteException
- **Interfaces**: Definição de contratos para operações bancárias

### 👥 Autores

Este projeto foi desenvolvido como trabalho académico de grupo.

### 🎓 Contexto Académico

**Unidade Curricular**: Linguagens de Programação
**Curso**: Gestão de Sistemas e Computação
**Ano Letivo**: 2024/2025
**Tipo**: Projeto Académico

Este projeto demonstra competências em:

- Desenvolvimento de software orientado a objetos
- Integração com bases de dados relacionais
- Programação concorrente
- Gestão de exceções e validações
- Documentação técnica e código limpo

### ⚠️ Notas de Utilização

Este sistema foi desenvolvido para fins educacionais e académicos. Não deve ser utilizado em ambientes de produção sem as devidas adaptações de segurança e escalabilidade.

---

## English

### 📋 Project Description

This academic project consists of the development of a **Bank Management System** that simulates real banking operations, implemented in Java. The system was developed within the scope of the **Programming Languages** course unit of the **Systems and Computing Management** degree.

The system enables complete management of customers and bank accounts, supporting fundamental operations such as deposits, withdrawals, transfers, and balance inquiries, with data persistence in SQLite relational database and auditing through structured XML logs.

### 🎯 Academic Objectives

- Apply advanced concepts of **Object-Oriented Programming** (OOP) in Java
- Implement design patterns such as **DAO (Data Access Object)** for separation of concerns
- Develop a transactional management system with support for atomic operations (commit/rollback)
- Integrate data persistence with SQLite using JDBC
- Implement concurrent processing with Java threads
- Create a robust auditing system with structured XML logging
- Ensure rigorous data validation and exception handling

### ✨ Main Features

#### 1. Customer Management
- Registration of new customers with complete data validation
- Portuguese NIF (Tax Identification Number) validation
- Minimum age validation for account opening
- Association of customers with bank accounts

#### 2. Bank Account Management
- Support for two account types:
  - **Current Account**: Allows overdraft up to a configurable limit
  - **Savings Account**: Periodic interest accrual applied automatically
- Fundamental banking operations:
  - Deposits
  - Withdrawals
  - Transfers between accounts
  - Balance and statement inquiry

#### 3. Transactional System
- Atomic transactions with **commit** and **rollback** support
- Data consistency guarantee through transactional control
- Thread-isolated transactions using **ThreadLocal**

#### 4. Data Persistence
- SQLite relational database
- DAO pattern for data access abstraction
- Automatic connection and resource management
- Referential integrity control

#### 5. Auditing and Logging
- Structured XML logging system (LogManager)
- Recording of all critical system operations
- Timestamp and user identification for each operation
- Facilitates auditing and operation traceability

#### 6. Background Processing
- Monitoring thread (MonitorContas) runs continuously
- Automatic interest application to savings accounts every 90 seconds
- Operations executed in background without blocking main interface

#### 7. User Interface
- Intuitive command-line interface (CLI)
- Interactive menus for navigation
- Real-time input validation
- Descriptive and helpful error messages

### 🏗️ System Architecture

```
Banco/
├── src/                           # Source code
│   ├── DAO/                       # Data Access Layer
│   │   ├── ClienteDAO.java        # Customer persistence operations
│   │   ├── ContaDAO.java          # Account persistence operations
│   │   ├── GestorBD.java          # Central database manager
│   │   └── TransacaoDAO.java      # Transaction persistence operations
│   ├── Interfaces/                # System Interfaces
│   │   └── Operacoes.java         # Banking operations interface
│   ├── Main/                      # Entry Point
│   │   └── Main.java              # Main class and CLI interface
│   ├── models/                    # Domain Models
│   │   ├── Cliente.java           # Customer model
│   │   ├── Conta.java             # Abstract account class
│   │   ├── ContaCorrente.java     # Current account implementation
│   │   ├── ContaPoupanca.java     # Savings account implementation
│   │   ├── Pessoa.java            # Abstract base class
│   │   ├── SaldoInsuficienteException.java  # Custom exception
│   │   └── Transacao.java         # Transaction model
│   └── services/                  # Service Layer
│       ├── LogManager.java        # XML log management
│       └── MonitorContas.java     # Monitoring thread
├── bin/                           # Compiled files (.class)
├── lib/                           # External libraries (JDBC SQLite)
├── banco.db                       # SQLite database (auto-created)
├── logs.xml                       # System logs (auto-created)
└── README.md                      # This file
```

The system follows a layered architecture with clear separation of concerns:

#### Model Layer (`models`)
- **Pessoa**: Abstract class representing individuals
- **Cliente**: Extension of Pessoa with banking data
- **Conta**: Abstract class for bank accounts
- **ContaCorrente** and **ContaPoupanca**: Specific account implementations
- **Transacao**: Record of banking operations
- **SaldoInsuficienteException**: Custom exception for balance control

#### Data Access Layer (`DAO`)
- **GestorBD**: Central database and connection manager
- **ClienteDAO**: Customer persistence operations
- **ContaDAO**: Account persistence operations
- **TransacaoDAO**: Transaction persistence operations

#### Interface Layer (`Interfaces`)
- **Operacoes**: Interface defining base banking operations

#### Service Layer (`services`)
- **LogManager**: XML format log management
- **MonitorContas**: Monitoring thread and interest application

#### Main Layer (`Main`)
- **Main**: System entry point and CLI interface

### 🔧 Technologies and Tools

| Technology | Usage | Version |
|-----------|-------|--------|
| **Java** | Main programming language | 8+ |
| **SQLite** | Relational database | 3.x |
| **JDBC** | Database connectivity API | - |
| **XML DOM** | XML processing and manipulation | - |
| **Java Threads** | Concurrent programming | Java Threading API |
| **ThreadLocal** | Thread-isolated connections | Java Concurrency |
| **Javadoc** | Code documentation | - |

### 💾 Database and Persistence

The system uses SQLite as a relational database management system, ensuring:

- **Atomicity**: Transactions are fully executed or rolled back
- **Consistency**: Validations ensure data integrity
- **Isolation**: ThreadLocal ensures isolated connections per thread
- **Durability**: Data persisted to disk

#### Database Schema

- **Clientes Table**: Stores customer information (NIF, name, age)
- **Contas Table**: Stores bank accounts (number, type, balance, holder)
- **Transacoes Table**: Historical record of all operations

The DAO pattern completely abstracts database access, facilitating maintenance and possible future migrations.

### ⚙️ Concurrency and Background Processing

#### ThreadLocal for Isolation
- Each thread maintains its own database connection
- Prevents conflicts in multi-threaded environments
- Guarantees transactional isolation

#### MonitorContas
- Daemon thread runs in background
- Periodically checks savings accounts (90 seconds)
- Automatically applies configurable interest rate
- Does not interfere with user operations

### 📝 XML Logging and Auditing

The system implements a robust auditing system through **LogManager**:

- Structured logs in XML format for easy parsing
- Recording of critical events:
  - Customer and account creation
  - Banking operations (deposits, withdrawals, transfers)
  - Errors and exceptions
- Each entry contains:
  - Precise timestamp
  - Operation type
  - Operation details
  - Result (success/failure)

### 🚀 How to Run the Project

#### Prerequisites
- Java Development Kit (JDK) 8 or higher installed
- JDBC driver for SQLite (included in `lib/`)

#### Execution Steps

1. **Compile the project**
   ```bash
   javac -d bin -cp "lib/*" src/**/*.java
   ```

2. **Run the application**
   ```bash
   java -cp "bin;lib/*" Main.Main
   ```

   On Linux/macOS, use `:` instead of `;`:
   ```bash
   java -cp "bin:lib/*" Main.Main
   ```

3. **Interact with the system**
   - Follow the menus presented in the CLI interface
   - Select operations using numbers
   - Enter data when prompted



### 📚 OOP Concepts Applied

- **Abstraction**: Abstract classes Pessoa and Conta
- **Encapsulation**: Access modifiers and getters/setters
- **Inheritance**: ContaCorrente and ContaPoupanca extend Conta
- **Polymorphism**: Implementation of Operacoes interface
- **Custom Exceptions**: SaldoInsuficienteException
- **Interfaces**: Definition of contracts for banking operations

### 👥 Authors

This project was developed as an academic group work.

### 🎓 Academic Context

**Course**: Programming Languages
**Degree**: Systems and Computing Management
**Academic Year**: 2024/2025
**Type**: Academic Project

This project demonstrates skills in:

- Object-oriented software development
- Integration with relational databases
- Concurrent programming
- Exception handling and validation
- Technical documentation and clean code

### ⚠️ Usage Notes

This system was developed for educational and academic purposes. It should not be used in production environments without appropriate security and scalability adaptations.

---

## 📄 License and Academic Usage

This project is part of an academic assignment. All rights reserved to the original authors. The code is provided for educational reference and learning purposes only.

---

**Course**: Linguagens de Programação | Programming Languages
**Degree**: Gestão de Sistemas e Computação | Systems and Computing Management
**Academic Year**: 2024/2025

---

<div align="center">

**Developed with ☕ and Java**

</div>
