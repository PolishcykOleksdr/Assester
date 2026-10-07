# Security and configuration

- Never hardcode passwords, signing keys, tokens, or other credentials. Bind secret configuration from environment variables or the deployment's secret provider.
- Encode passwords with the configured `PasswordEncoder`; never store or log raw passwords.
- Use Spring Security's configured authorization and CSRF protections. Check access control whenever adding or changing a route or state-changing operation.
- Use parameterized repository queries; do not build database queries by concatenating user input.
- Keep production data and secrets out of tests, logs, and committed configuration.
