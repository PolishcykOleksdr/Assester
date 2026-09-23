# 🤖 AGENTS.md — Assester Project Guidelines

## 1. Project Overview & Context
- **Project Name:** Assester (`com.order.platform:Assester`)
- **Tech Stack:** Java 21, Spring Boot 4.0.0, Spring Security, Spring WebMVC, Spring Validation, Thymeleaf, PostgreSQL, Maven Wrapper.
- **Current State:** Initial stage. Authentication endpoint skeleton exists (`/login`), but core logic, views, and security configuration are pending implementation.

---

## 2. Technical Standards & Code Guidelines

### Architecture Rules:
1. **Layer Separation:** Keep Controllers, Services, Repositories, DTOs, and Domain Entities strictly isolated.
2. **DTO Policy:** Always use DTOs (Records preferred, e.g., `LoginUserDto`) for request payloads and API responses. Never expose JPA Entities directly to controllers or views.
3. **Dependency Injection:** Use constructor injection (`@RequiredArgsConstructor` via Lombok or explicit constructors). Avoid `@Autowired` on fields.
4. **Naming Conventions:** Ensure all package names, class names, and methods follow standard English spelling and CamelCase convention.

### Identified Issues to Fix Immediately:
- **Typo Fix:** Rename `com.order.platform.assester.config.SecuirtyConfig` to `SecurityConfig`.
- **Logic Implementation:** Implement business logic inside `LoginController.post()` and back it with a proper service layer.

---

## 3. Agent Roles & Responsibilities

### 🔹 Orchestrator / Planner Agent (`plan` mode)
- **Role:** Analyzes requests, evaluates the code tree, and creates step-by-step feature implementation plans.
- **Permissions:** Read-only access (`write: deny`, `edit: deny`).
- **Primary Tasks:** Project architecture design, database schema planning, security flow mapping.

### 🔹 Backend Developer Agent (`build` mode)
- **Role:** Implements Java classes, Spring Security configurations, REST controllers, DTOs, and Spring Data repositories.
- **Constraints:** Must validate inputs using `spring-boot-starter-validation` (`@Valid`, `@NotNull`, etc.). Must handle exceptions with `@RestControllerAdvice`.
- **Tools Allowed:** `file_editor`, `terminal_exec` (`./mvnw compile`), `write`.

### 🔹 Frontend / Template Agent
- **Role:** Creates and maintains HTML5 pages inside `src/main/resources/templates/` using Thymeleaf and Spring Security dialect (`thymeleaf-extras-springsecurity6`).
- **Constraints:** Keep CSS/JS organized in `src/main/resources/static/`.

### 🔹 Code Quality & Security Reviewer (`@code-reviewer`)
- **Role:** Audits proposed changes before committing.
- **Focus Areas:**
    - Proper Spring Security filter chain configuration.
    - SQL injection prevention, safe password encoding (`PasswordEncoder`).
    - Code syntax, proper HTTP response codes, and absence of typos.

---

## 4. Workflows & Commands

When instructing agents to run project tasks, use the embedded Maven Wrapper:

- **Build Project:** `./mvnw clean package` (or `mvnw.cmd clean package` on Windows)
- **Run Application:** `./mvnw spring-boot:run`
- **Execute Tests:** `./mvnw test`

---

## 5. Safety & Security Constraints

1. **Secrets & Credentials:** NEVER hardcode database passwords, JWT secrets, or API keys in `application.yaml` or Java code. Use environment variables.
2. **Destructive Commands:** Executing `rm -rf`, dropping production schemas, or altering Git history without explicit user confirmation is STRICTLY PROHIBITED.
3. **Validation:** Ensure all new endpoints accepting incoming JSON or Form data are protected with Jakarta Validation annotations.