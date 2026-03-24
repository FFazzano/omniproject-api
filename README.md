# 🚀 Omniproject

<img width="1600" height="789" alt="image" src="https://github.com/user-attachments/assets/c3bfc294-ba73-40f6-9e91-6ada32e28c4c" />


> Um sistema Fullstack completo para gerenciamento de workspaces e tarefas, construído com foco em segurança, escalabilidade e boas práticas de engenharia de software.

## 💻 Sobre o Projeto

O Omniproject é uma aplicação desenvolvida para organizar projetos e tarefas em tempo real. A aplicação conta com um painel interativo (dashboard) onde os usuários podem criar workspaces, adicionar tarefas e acompanhar o progresso através de uma barra de status dinâmica. 

O foco principal do desenvolvimento foi construir um **Back-end robusto e seguro**, implementando autenticação via JWT, proteção de rotas, relacionamento de entidades e tratamento de exceções, servindo uma API RESTful consumida por uma interface em React.

## ⚙️ Destaques da Arquitetura Back-end

* **Segurança Avançada:** Implementação de Spring Security com autenticação Stateless e validação de tokens JWT personalizados.
* **Modelagem de Dados:** Relacionamentos complexos (`@OneToMany`, `@ManyToMany`) estruturados no Hibernate/JPA, resolvendo problemas clássicos de recursão infinita no JSON com `@JsonManagedReference` e `@JsonBackReference`.
* **Banco de Dados na Nuvem:** Integração com PostgreSQL hospedado no Neon, otimizado para ambientes de produção e desenvolvimento.
* **Ambientes Isolados (Sandbox):** Configuração profissional de variáveis de ambiente (`.env`) para separar o ambiente de desenvolvimento local (localhost) do ambiente de produção (Render).
* **Integração com IA (Gemini API):** Implementação do TrixService para orquestração de prompts inteligentes e processamento de dados.
* **Tratamento de Exceções Global:** Implementação de um GlobalExceptionHandler para garantir respostas de erro padronizadas e seguras na API.

## 🛠️ Tecnologias Utilizadas

**Back-end:**
* Java 21
* Spring Boot 3
* Spring Security & JWT
* Spring Data JPA / Hibernate
* PostgreSQL (Neon DB)
* Maven

**Front-end:**
* React (Vite)
* React Router Dom
* Axios
* Tailwind CSS / Lucide React

## 🚀 Como abrir o projeto
https://omniproject-frontend.vercel.app/dashboard
