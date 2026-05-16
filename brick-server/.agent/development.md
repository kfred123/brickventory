# Development Guidelines

## Git / Version Control

> **IMPORTANT: Never commit or push on your own. Commits and pushes are always done by the user.**

- Repository: https://github.com/kfred123/brick-server
- Branch: `main`

## Running the Application

1. **Start the database:**
   ```
   docker compose up -d
   ```
2. **Run the application:**
   ```
   mvn spring-boot:run
   ```
   The server starts on `http://localhost:8080`.

3. **Build without running:**
   ```
   mvn clean compile
   ```

## Database

- PostgreSQL runs via Docker on port 5432
- Credentials: `brickdb` / `brickuser` / `brickpassword` (see `docker-compose.yml`)
- Schema managed by **Flyway** — migrations live in `src/main/resources/db/migration/`
- Hibernate is set to `validate` mode — it does NOT auto-generate DDL
- To fully reset the DB, run `src/main/resources/db/manual/drop_all.sql` manually, then restart the app

### Migration Rules

- New schema changes require a new Flyway migration file (`V2__description.sql`, `V3__...`, etc.)
- **Never** modify an existing migration file that has already been applied
- If the user says "drop the DB" or "no migration needed", update `V1` directly and the user will reset manually

## Code Conventions

- **Language:** Kotlin — use idiomatic Kotlin (data classes, null safety, extension functions)
- **Package:** `com.example.brickserver`
- **Entities:** extend `BaseEntity` (provides `id: UUID`, `createdAt`, `updatedAt`)
- **Table naming:** snake_case (`brick_sets`, `user_bricks`)
- **Entity naming:** PascalCase (`BrickSet`, `UserBrick`)
- **Repositories:** named after entity + "Repository" suffix (`BrickSetRepository`)
- **Controllers:** grouped by feature, REST conventions (`@GetMapping`, `@PostMapping`, etc.)
- **DTOs:** placed in `controller/dto/` package, use companion `from()` factory methods

## Testing

- No tests exist yet. Test directory: `src/test/kotlin/`
- Test dependency is already in `pom.xml` (`spring-boot-starter-test`)

## Authentication

- JWT-based authentication via Spring Security
- Public routes are explicitly permitted in `SecurityConfig.kt`
- Authenticated routes require `Authorization: Bearer <token>` header
- JWT secret and expiration configured in `application.yml` under `app.jwt`
