---
name: spring-controller
description: Standards and template for writing REST Controllers in Spring Boot.
---

# Spring Boot REST Controller Guidelines

Whenever creating or updating a `@RestController`:

1. **Mapping:** Use class-level `@RequestMapping("/api/v1/resource")`.
2. **Responses:** Wrap all responses in `ResponseEntity<T>`.
3. **Validation:** Always validate incoming payloads using `@Valid` and Jakarta Validation annotations.
4. **DTO Mapping:** Map entities to DTOs in the service layer, not in the controller.
5. **OpenAPI/Swagger:** Include `@Operation` and `@ApiResponse` annotations for documentation.