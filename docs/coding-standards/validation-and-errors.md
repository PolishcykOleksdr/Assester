# Input validation and errors

- Validate user-supplied form and request data with Jakarta Validation constraints, and trigger validation at the controller boundary (for example, with `@Valid`).
- Return useful, appropriate errors through the application's centralized exception handling. Do not leak stack traces, secrets, or internal implementation details to users.
- Keep validation rules close to the relevant DTO or domain operation; enforce business invariants in the service/domain layer as well when they must hold for every entry point.
