-- Initial schema for Brick Server (Consolidated)
-- Creates tables for bricks, brick images, brick sets, and brick set parts
-- Uses UUIDs for primary keys
-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- Table: bricks
CREATE TABLE bricks (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    part_num VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
-- Table: brick_sets
CREATE TABLE brick_sets (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    source VARCHAR(255) NOT NULL,
    set_num VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    year_released INTEGER,
    theme_id INTEGER,
    num_parts INTEGER,
    set_img_url VARCHAR(500),
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (source, set_num)
);
-- Table: brick_images
CREATE TABLE brick_images (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    brick_id UUID NOT NULL,
    image_path VARCHAR(500) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (brick_id) REFERENCES bricks(id) ON DELETE CASCADE
);
-- Table: brick_set_parts
CREATE TABLE brick_set_parts (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    set_id UUID,
    brick_id UUID,
    quantity INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (set_id) REFERENCES brick_sets(id) ON DELETE CASCADE,
    FOREIGN KEY (brick_id) REFERENCES bricks(id) ON DELETE CASCADE
);
-- Indexes for performance
CREATE INDEX idx_brick_images_brick_id ON brick_images(brick_id);
CREATE INDEX idx_brick_set_parts_set_id ON brick_set_parts(set_id);
CREATE INDEX idx_brick_set_parts_brick_id ON brick_set_parts(brick_id);
-- Table: users
CREATE TABLE users (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
-- Table: user_bricks
CREATE TABLE user_bricks (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    brick_id UUID NOT NULL REFERENCES bricks(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL DEFAULT 1,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, brick_id)
);
-- Table: user_sets
CREATE TABLE user_sets (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    set_id UUID NOT NULL REFERENCES brick_sets(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL DEFAULT 1,
    notes VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, set_id)
);
CREATE INDEX idx_user_bricks_user_id ON user_bricks(user_id);
CREATE INDEX idx_user_sets_user_id ON user_sets(user_id);