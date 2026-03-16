# Java CRM Platform

<div align="center">

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring_Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![License-MIT](https://img.shields.io/badge/License--MIT-yellow?style=for-the-badge)

</div>


[English](#english) | [Portugues](#portugues)

---

## Portugues

Plataforma de CRM (Customer Relationship Management) em Java com gestao de clientes, rastreamento de interacoes, pipeline de vendas com estagios e analises de desempenho comercial.

### Arquitetura

```mermaid
graph TD
    A[CrmPlatform] --> B[Customer Management]
    A --> C[Interaction Tracking]
    A --> D[Sales Pipeline]
    A --> E[Analytics Engine]
    B --> F[Customer]
    B --> G[Segmentation]
    C --> H[Email]
    C --> I[Call]
    C --> J[Meeting]
    C --> K[Support Ticket]
    D --> L[Prospecting]
    D --> M[Qualification]
    D --> N[Proposal]
    D --> O[Negotiation]
    D --> P[Closed Won/Lost]
    E --> Q[Pipeline Value]
    E --> R[Revenue Reports]
    E --> S[Segment Analysis]
```

### Funcionalidades

- Gestao completa de clientes com segmentacao (Enterprise, SMB, Mid-Market)
- Rastreamento de interacoes: email, chamada, reuniao, ticket de suporte
- Pipeline de vendas com estagios e probabilidade ponderada
- Busca de clientes por nome, email ou empresa
- Analises: valor do pipeline, receita fechada, distribuicao por segmento
- Calculo automatico de Lifetime Value (LTV) do cliente

### Como Executar

```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.galafis.crm.CrmPlatform"
```

---

## English

CRM (Customer Relationship Management) platform in Java with customer management, interaction tracking, sales pipeline with stages, and commercial performance analytics.

### Architecture

```mermaid
graph TD
    A[CrmPlatform] --> B[Customer Management]
    A --> C[Interaction Tracking]
    A --> D[Sales Pipeline]
    A --> E[Analytics Engine]
    B --> F[Customer]
    B --> G[Segmentation]
    C --> H[Email]
    C --> I[Call]
    C --> J[Meeting]
    C --> K[Support Ticket]
    D --> L[Prospecting]
    D --> M[Qualification]
    D --> N[Proposal]
    D --> O[Negotiation]
    D --> P[Closed Won/Lost]
    E --> Q[Pipeline Value]
    E --> R[Revenue Reports]
    E --> S[Segment Analysis]
```

### Features

- Full customer management with segmentation (Enterprise, SMB, Mid-Market)
- Interaction tracking: email, call, meeting, support ticket
- Sales pipeline with stages and weighted probability
- Customer search by name, email, or company
- Analytics: pipeline value, closed revenue, segment distribution
- Automatic customer Lifetime Value (LTV) calculation

### How to Run

```bash
mvn compile
mvn exec:java -Dexec.mainClass="com.galafis.crm.CrmPlatform"
```

## Author

Gabriel Demetrios Lafis

## License

MIT License
