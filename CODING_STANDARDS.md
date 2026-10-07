# Coding standards

Read the sections relevant to your change. Keep the existing architecture and conventions consistent; update these rules when the project adopts a new convention.

## Java and Spring

- Target Java 21 and the Spring Boot versions and dependencies declared in `pom.xml`.
- Keep web/controller, service, repository, DTO, and entity responsibilities separate. Controllers handle HTTP concerns; services own application logic; repositories handle persistence.
- Use request and response DTOs at controller boundaries. Do not expose JPA entities directly to controllers' views or API responses. Prefer records for immutable DTOs where binding and validation requirements allow them.
- Use constructor injection, either explicit constructors or Lombok's `@RequiredArgsConstructor`; do not inject dependencies into fields.
- Use clear English names and standard Java naming conventions.

## Input validation and errors

- Validate user supplied form and request data with Jakarta Validation constraints, and trigger validation at the controller boundary (for example, with `@Valid`).
- Return useful, appropriate errors through the application's centralized exception handling. Do not leak stack traces, secrets, or internal implementation details to users.
- Keep validation rules close to the relevant DTO or domain operation; enforce business invariants in the service/domain layer as well when they must hold for every entry point.

## Security and configuration

- Never hardcode passwords, signing keys, tokens, or other credentials. Bind secret configuration from environment variables or the deployment's secret provider.
- Encode passwords with the configured `PasswordEncoder`; never store or log raw passwords.
- Use Spring Security's configured authorization and CSRF protections. Check access control whenever adding or changing a route or state-changing operation.
- Use parameterized repository queries; do not build database queries by concatenating user input.
- Keep production data and secrets out of tests, logs, and committed configuration.

## Thymeleaf and static assets

- Put server-rendered pages in `src/main/resources/templates/` and CSS/JavaScript in `src/main/resources/static/`.
- Pass view models or DTOs to templates, not JPA entities. Escape and render user-controlled content safely using Thymeleaf's standard escaped expressions.
- Keep presentation logic in templates minimal; put reusable behavior in static assets and application decisions in services.
