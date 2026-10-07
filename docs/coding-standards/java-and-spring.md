# Java and Spring

- Target Java 21 and the Spring Boot versions and dependencies declared in `pom.xml`.
- Keep web/controller, service, repository, DTO, and entity responsibilities separate. Controllers handle HTTP concerns; services own application logic; repositories handle persistence.
- Use request and response DTOs at controller boundaries. Do not expose JPA entities directly to controllers' views or API responses. Prefer records for immutable DTOs where binding and validation requirements allow them.
- Use constructor injection, either explicit constructors or Lombok's `@RequiredArgsConstructor`; do not inject dependencies into fields.
- Use clear English names and standard Java naming conventions.
