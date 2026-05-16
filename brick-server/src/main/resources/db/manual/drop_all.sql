-- Manual script to drop all tables and types
-- Execute this script manually in your database client (e.g. IntelliJ Database tool, pgAdmin, psql)
-- This will NOT be executed automatically by Flyway.
DROP TABLE IF EXISTS user_sets CASCADE;
DROP TABLE IF EXISTS user_bricks CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS brick_set_parts CASCADE;
DROP TABLE IF EXISTS brick_images CASCADE;
DROP TABLE IF EXISTS brick_sets CASCADE;
DROP TABLE IF EXISTS bricks CASCADE;
DROP TABLE IF EXISTS flyway_schema_history CASCADE;
-- Drop extension if you want to fully reset (usually needs superuser)
-- DROP EXTENSION IF EXISTS "uuid-ossp";