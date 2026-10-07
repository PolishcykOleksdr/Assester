# Thymeleaf and static assets

- Put server-rendered pages in `src/main/resources/templates/` and CSS/JavaScript in `src/main/resources/static/`.
- Pass view models or DTOs to templates, not JPA entities. Escape and render user-controlled content safely using Thymeleaf's standard escaped expressions.
- Keep presentation logic in templates minimal; put reusable behavior in static assets and application decisions in services.
