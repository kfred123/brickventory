# Brick Server

Brick Server is a Spring Boot REST API for managing a brick (Lego-compatible) inventory — individual parts, sets, images, and user collections.

## Tech Stack

- **Language:** Kotlin 1.9, JVM 21
- **Framework:** Spring Boot 3.2.1
- **Build:** Maven (`pom.xml`)
- **Database:** PostgreSQL 16 (via Docker)
- **ORM:** Spring Data JPA / Hibernate (validate mode)
- **Migrations:** Flyway (`src/main/resources/db/migration/`)
- **Auth:** Spring Security + JWT (jjwt 0.12.3)
- **PDF Processing:** Apache PDFBox 3.0.1

## Project Structure

```
src/main/kotlin/com/example/brickserver/
├── BrickServerApplication.kt        # Entry point
├── config/
│   ├── SecurityConfig.kt            # Spring Security setup, route protection
│   ├── JwtAuthenticationFilter.kt   # JWT token filter
│   └── RebrickableConfig.kt         # External API config
├── controller/
│   ├── AuthController.kt            # POST /api/auth/register, /api/auth/login
│   ├── BrickController.kt           # GET /api/bricks, /api/bricks/{id}, /api/bricks/images/{id}
│   ├── BrickSetController.kt        # GET /api/sets, POST /api/inventory/add
│   ├── ImageController.kt           # Image upload endpoint
│   ├── MaintenanceController.kt     # Bulk CSV/ZIP import endpoints
│   ├── UserCollectionController.kt  # CRUD /api/me/collection/bricks, /api/me/collection/sets
│   └── dto/                         # Request/response DTOs
├── domain/
│   ├── BaseEntity.kt                # Shared UUID + timestamps
│   ├── Brick.kt, BrickImage.kt, BrickSet.kt, BrickSetPart.kt
│   ├── User.kt, UserBrick.kt, UserSet.kt
├── repository/                      # Spring Data JPA repositories
├── service/
│   ├── AuthService.kt               # Registration + login logic
│   ├── JwtService.kt                # JWT generation/validation
│   ├── ImageService.kt              # Image storage
│   ├── PdfParsingService.kt         # PDF text+image extraction
│   └── PdfImageExtractor.kt         # Low-level PDF image extraction
```

## Key Configuration Files

| File | Purpose |
|------|---------|
| `pom.xml` | Maven dependencies and build config |
| `docker-compose.yml` | PostgreSQL container (brickdb/brickuser/brickpassword) |
| `src/main/resources/application.yml` | App config, DB connection, JWT secret |
| `src/main/resources/db/migration/V1__Initial_schema.sql` | Full database schema |
| `src/main/resources/db/manual/drop_all.sql` | Manual DB reset script |

## Database Tables

`bricks`, `brick_sets`, `brick_set_parts`, `brick_images`, `users`, `user_bricks`, `user_sets`

All tables use UUID primary keys. Schema managed by Flyway.

## API Routes

**Public:**
- `POST /api/auth/register` — register new user
- `POST /api/auth/login` — get JWT token
- `GET /api/bricks` — list/search bricks
- `GET /api/sets` — list sets (optionally by year)

**Authenticated (JWT required):**
- `GET/POST/PUT/DELETE /api/me/collection/bricks` — user's brick collection
- `GET/POST/PUT/DELETE /api/me/collection/sets` — user's set collection
- `POST /api/maintenance/*` — bulk data import endpoints
