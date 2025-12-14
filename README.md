# 🍺 Beer Brewery Stock Manager

<p align="center">
  Um **Sistema de Gestão de Estoque Inteligente e Orientado a Dados**, projetado para cervejarias.
</p>
<p align="center">
  Este projeto simula um ambiente corporativo real, servindo como um **Laboratório Prático de Engenharia de Software** que cobre o ciclo completo de desenvolvimento (Análise, Design, Desenvolvimento, DevSecOps e Infraestrutura Cloud).
</p>

---
## 🌟 Visão Geral e Contexto do Projeto (Laboratório)

O Beer Brewery Stock Manager é um sistema de backend responsável pelo controle de estoque de cervejas, utilizando rastreamento em tempo real, alertas de validade, sugestões de reposição e análise inteligente da demanda de produtos.

### 🎯 Objetivos de Aprendizado e Escopo

Este projeto não se limita ao código. Ele é um esforço para implementar as melhores práticas em todas as fases:

| Fase | Foco e Conceitos Implementados | Status |
| :--- | :--- | :--- |
| **Documentação & Design** | Análise de Negócios, Levantamento de Requisitos, Diagramas de Classe e Casos de Uso. | ✅ Concluído |
| **Desenvolvimento** | Aplicação de padrões Spring, Segurança (OAuth2/Resource Server), e Validação. | ✅ Concluído |
| **Qualidade & Observabilidade** | Testes Unitários e Integração, Cobertura de Código (**Jacoco**), Rastreamento (**Micrometer Tracing/Brave**), Métricas (**Prometheus/Actuator**). | ✅ Concluído |
| **DevSecOps & CI/CD** | Fluxo de trabalho (Git Flow), Pipelines automatizadas via **GitHub Actions**. | ✅ Concluído |
| **Infraestrutura Cloud** | Design de Arquitetura em Nuvem (AWS) e consolidação da infraestrutura. | ✅ Concluído (Fase de IaC em andamento) |

---

## 🏗️ Arquitetura Atual (Monolito em AWS)

O sistema está atualmente implementado como um monolito, mas projetado para **Alta Disponibilidade (HA), Escalabilidade e Segurança** na AWS.

### Diagrama de Arquitetura AWS

A infraestrutura foi desenhada seguindo o modelo de referência Multi-Camadas da AWS:

[Você pode inserir a imagem do diagrama AWS aqui, se o GitHub permitir renderizar imagens da sua pasta `/docs` diretamente. Caso contrário, mantenha o link.]

[🔗 Visualizar Diagrama da Arquitetura AWS](https://github.com/study-group-anapedra/brewery-stock-manager/blob/develop/docs/diagrama-arquitetura-aws.png)

A Stack utiliza: **AWS WAF**, **CloudFront**, **Route 53**, **Application Load Balancers (ALB)**, **Auto Scaling Groups (ASG)**, **NAT Gateway**, **Amazon RDS Multi-AZ** e **Amazon ElastiCache**.

---

## 🔮 Visão Futura e Próximos Desafios

As próximas etapas de evolução do projeto focam em migrar para uma arquitetura moderna de microserviços e melhorar o design do código:

1.  **Evolução para Microserviços:** Utilizar a **Tática do Estrangulador** (*Strangler Fig Pattern*) para migrar de forma segura e gradual a arquitetura monolítica para serviços menores e independentes.
2.  **Arquitetura de Código:** Adotar o modelo **Arquitetura Hexagonal** (*Ports and Adapters*), visando maior desacoplamento, testabilidade e manutenibilidade.

---

## 🚀 Tecnologias Utilizadas

### **Backend (Java/Spring Boot)**
* **Java 21**
* **Spring Boot 3.4.3** (Web / MVC, Data JPA, Validation)
* **Spring Security:** Implementação de **OAuth2 Authorization Server** e Resource Server.
* **Spring WebFlux** (Suporte Reativo)
* **Documentação:** **Swagger / OpenAPI 3**
* **Banco de Dados:** **PostgreSQL** (Flyway) e H2 (para testes)

### **DevOps e Observabilidade**
* **CI/CD:** **GitHub Actions**
* **Métricas:** **Micrometer/Prometheus** (via Actuator)
* **Rastreamento:** **Micrometer Tracing** (Brave)
* **Logging:** **Logstash** encoder
* **Build:** **Maven**
* **Qualidade:** **Jacoco** (Relatórios de cobertura de código)

---

## 📄 Artefatos e Documentação Técnica

| Documento | Descrição | Link |
| :--- | :--- | :--- |
| Análise de Negócio | Contexto, problema central e visão do sistema. | 🔗 [Análise de Negócio] |
| Levantamento de Requisitos | Requisitos funcionais e não funcionais. | 🔗 [Levantamento de Requisito] |
| Caso de Uso | Diagramas e especificações de uso do sistema. | 🔗 [Caso de Uso] |
| Diagrama de Classe | Estrutura e relacionamentos do modelo de domínio. | 🔗 [Diagrama de Classe] |
| Plano e Cenários de Teste | Estratégia de teste e cenários detalhados. | 🔗 [Plano de Teste], [Cenário de Teste] |
| Arquitetura AWS | Diagrama de implementação de HA e escalabilidade na AWS. | 🔗 [Diagrama Arquitetura AWS] |

*(Links para os arquivos devem ser corrigidos, se necessário: substitua `[Nome]` pelo link completo)*

---

## 📂 Estrutura do Projeto