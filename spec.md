# Brickventory Specification

This document serves as the single source of truth for the Brickventory project's architecture, data models, and features. It represents the current state of the application.

## Architecture
- **Repository**: Monorepo containing both the frontend (`brick-app`) and backend (`brick-server`).
- **Backend Framework**: Kotlin with Spring Boot
- **Database**: PostgreSQL
- **Migrations**: Flyway (`src/main/resources/db/migration`)
- **Identifiers**: UUIDs (`gen_random_uuid()`) are used for all primary keys.

## Data Models

### Bricks (`bricks`)
- Represents individual brick parts.
- Fields: `id`, `partNum` (unique), `name`.

### Brick Images (`brick_images`)
- Tracks images associated with specific bricks.
- Fields: `id`, `brickId`, `imagePath`, `isVerified`.

### Brick Sets (`brick_sets`)
- Represents official sets (e.g., Lego, BlueBrixx).
- Fields: `id`, `source`, `setNum`, `name`, `yearReleased`, `themeId`, `numParts`, `setImgUrl`, `description`.
- `source` and `setNum` form a unique constraint.

### Brick Set Parts (`brick_set_parts`)
- Represents the inventory of a set.
- Links a `BrickSet` to a `Brick` with a `quantity`.

### Users & Collection
- **Users (`users`)**: Standard user account with `email`, `password_hash`, and `display_name`.
- **User Bricks (`user_bricks`)**: Tracks individual bricks owned by a user (`quantity`, `notes`). Links a `User` to a `Brick`.
- **User Sets (`user_sets`)**: Tracks complete sets owned by a user (`quantity`, `notes`). Links a `User` to a `BrickSet`.

## API Endpoints

### Authentication (`/api/auth`)
- `POST /register`: Register a new user.
- `POST /login`: Authenticate a user and return credentials/tokens.

### Bricks (`/api/bricks`)
- `GET /`: Retrieve a list of bricks.
- `GET /{brickId}`: Retrieve details of a specific brick.
- `GET /images/{imageId}`: Retrieve a specific brick image.

### Brick Sets (`/api/sets`)
- `GET /`: Retrieve brick sets (supports filtering, e.g., by `year`).
- `GET /{setId}`: Retrieve detailed set information including its parts inventory.
- `PUT /{setId}`: Update set details (`name`, `description`).

### User Collection (`/api/user`)
- `GET /bricks`: Retrieve bricks in the user's collection.
- `POST /bricks`: Add a brick to the collection.
- `PUT /bricks/{brickId}`: Update quantity/notes of a collected brick.
- `DELETE /bricks/{brickId}`: Remove a brick from the collection.
- `GET /sets`: Retrieve sets in the user's collection.
- `POST /sets`: Add a set to the collection.
- `PUT /sets/{setId}`: Update quantity/notes of a collected set.
- `DELETE /sets/{setId}`: Remove a set from the collection.

### Images (`/api/images`)
- `POST /upload`: Upload a new image file.

### Maintenance (`/api/maintenance`)
- Endpoints for maintenance operations (e.g., data synchronization or background tasks).
