---
name: unit-testing
description: Best practices for writing unit tests using JUnit 5, Mockito, and AssertJ.
---

# Unit Testing Standards

When writing unit tests for Spring Boot components:

1. **Naming:** Use readable test method names: `given[Context]_when[Action]_then[ExpectedResult]()`.
2. **Structure:** Structure test methods using the AAA pattern: **Arrange, Act, Assert**.
3. **Mocking:** Use `@ExtendWith(MockitoExtension.class)` for service unit tests without loading full Spring context.
4. **Assertions:** Prefer AssertJ (`assertThat(...)`) over standard JUnit assertions for better readability.