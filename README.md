# Assester

Assester is a learning platform for publishing courses and sharing standalone learning materials. A material can include several downloadable files and can also be included in a course.

The application uses Spring Boot, Spring Security with JWT cookies, Thymeleaf, and PostgreSQL. It serves server-rendered HTML pages; it does not currently expose a separate public REST API.

## Features

- Browse and search published courses.
- Publish standalone materials and share them by material code.
- Attach up to five files to a material and download them from its public page.
- Include your published materials in your courses.
- Manage courses through draft and review states.

## Run with Docker Compose

1. Create a `.env` file in the project root and set the values you need. See [Configuration](#configuration).
2. Build and start the application:

   ```sh
   docker compose up --build -d
   ```

3. Open `http://localhost:8080` (or the port configured by `APP_PORT`).

To stop the containers, run `docker compose down`. PostgreSQL data is kept in the `postgres_data` Docker volume. Uploaded material files are kept in `./uploads` on the host; logs are written to `./logs`.

## Configuration

| Variable                    | Purpose | Default |
|-----------------------------| --- | --- |
| `APP_PORT`                  | Port exposed by the application | `8080` |
| `POSTGRES_USER`             | PostgreSQL username | `postgres` |
| `POSTGRES_PASSWORD`         | PostgreSQL password | `rootroot` |
| `POSTGRES_DB`               | PostgreSQL database name | `assester` |
| `POSTGRES_PORT`             | PostgreSQL host port | `5432` |
| `POSTGRES_URL`              | Full JDBC URL override | Compose configures this automatically |
| `JWT_SECRET`                | JWT signing key | Development default in configuration; set a strong secret outside local development |
| `JWT_EXPIRATION`            | JWT lifetime in milliseconds | `86400000` |
| `AUTH_TOKEN_NAME`           | Authentication cookie name | `AUTH_TOKEN` |
| `JWT_SECURE_COOKIE`         | Set the cookie's Secure flag when using HTTPS | `false` |
| `JPA_SHOW_SQL`              | Show generated SQL in logs | `false` in Compose |
| `MATERIAL_STORAGE_LOCATION` | Directory where uploaded files are stored | `/app/uploads/materials` in Compose |
| `MATERIAL_MAX_FILE_SIZE`    | Maximum size of one uploaded file | `25MB` |
| `MATERIAL_MAX_REQUEST_SIZE` | Maximum size of one upload request | `125MB` |
| `SPRING_PROFILES_ACTIVE`    | Spring profile | `dev` |

For local runs outside Docker, the material storage default is `uploads/materials`, relative to the application's working directory. In Compose, `./uploads` is mounted into the app container so uploaded files persist across container recreation. The upload directory is excluded from Git.

## HTTP endpoints

All pages below return server-rendered HTML unless noted otherwise. Routes under `/my/**` require authentication. The admin page requires the `ADMIN` role. Public catalog and material routes show published content only.

### Home and authentication

| Method | Path | Access | Purpose |
| --- | --- | --- | --- |
| `GET` | `/` | Public | Home page |
| `GET` | `/register` | Public | Registration form |
| `POST` | `/register` | Public | Create an account and sign in |
| `GET` | `/login` | Public | Sign-in form |
| `POST` | `/login` | Public | Sign in and set the JWT cookie |
| `POST` | `/logout` | Public | Clear the authentication cookie and return home |

### Courses

| Method | Path | Access | Purpose |
| --- | --- | --- | --- |
| `GET` | `/catalog` | Public | Browse published courses; accepts `query` or `code` query parameters |
| `GET` | `/my/courses` | Authenticated | List your courses |
| `GET` | `/my/courses/new` | Authenticated | Open the course creation form |
| `POST` | `/my/courses` | Authenticated | Create a course draft; may include repeated `materialIds` form fields |
| `GET` | `/my/courses/{courseId}/edit` | Course owner | Edit a draft or a course with requested changes |
| `POST` | `/my/courses/{courseId}` | Course owner | Save course changes |
| `POST` | `/my/courses/{courseId}/submit` | Course owner | Submit a course for review |

Course search examples: `/catalog?query=biology` and `/catalog?code=CRS-1234ABCD`.

### Materials

| Method | Path | Access | Purpose |
| --- | --- | --- | --- |
| `GET` | `/materials` | Public | Browse published materials; accepts `query` or `code` query parameters |
| `GET` | `/materials/code/{code}` | Public | Resolve a material code and redirect to its page |
| `GET` | `/materials/{id}` | Public | View material details and attached files |
| `GET` | `/materials/{materialId}/files/{fileId}` | Public | Download a file attached to a published material |
| `GET` | `/my/materials` | Authenticated | List materials you have published |
| `GET` | `/my/materials/new` | Authenticated | Open the material publishing form |
| `POST` | `/my/materials` | Authenticated | Publish a material with 1–5 files (multipart form) |

Material search examples: `/materials?query=chemistry` and `/materials?code=MAT-1234ABCD`. Allowed file extensions are PDF, PowerPoint (`.ppt`, `.pptx`, `.odp`), Word/OpenDocument (`.doc`, `.docx`, `.odt`), text (`.txt`), and images (`.png`, `.jpg`, `.jpeg`). The maximum is 25 MB per file by default.

### Administration and static assets

| Method | Path | Access | Purpose |
| --- | --- | --- | --- |
| `GET` | `/admin` | Admin | Admin page |
| `GET` | `/css/**`, `/js/**`, `/favicon.ico` | Public | Static application assets |

Form submissions use Spring Security's CSRF protection. Include the CSRF token when submitting forms outside the provided pages.

## Project layout

```text
src/main/java/.../controllers   HTTP routes and page controllers
src/main/java/.../services      Application logic
src/main/java/.../entities      JPA entities
src/main/java/.../repositories  Database access
src/main/resources/templates    Thymeleaf pages
src/main/resources/static       CSS, JavaScript, and static assets
```

## Development notes

- Java 21 is required; Maven Wrapper scripts are included (`./mvnw` or `mvnw.cmd`).
- Hibernate schema management currently uses `ddl-auto: update`.
- Uploaded files are stored on disk, while their metadata is stored in PostgreSQL. Back up both the database and `uploads/` when preserving user data.
- See [CODING_STANDARDS.md](CODING_STANDARDS.md) for implementation guidance.
