---
name: thymeleaf-ui
description: Standards and guidelines for creating responsive Thymeleaf HTML templates and styling forms in Spring Boot.
---

# Thymeleaf UI & Frontend Standards

When building or updating user interfaces in `src/main/resources/templates/`:

## 1. Template Structure
- Use standard HTML5 boilerplate with `<meta name="viewport" content="width=device-width, initial-scale=1.0">`.
- Import static resources using Thymeleaf context path syntax:
  ```html
  <link rel="stylesheet" th:href="@{/css/main.css}">
  <script th:src="@{/js/app.js}" defer></script>