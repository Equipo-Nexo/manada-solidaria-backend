-- Migration: Create report_post table for content moderation
-- Version: 202611010100

-- 1. Crear la tabla report_post
CREATE TABLE report_post (
    id BINARY(16) NOT NULL,
    post_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_report_post_user_post UNIQUE (post_id, user_id),
    CONSTRAINT fk_report_post_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================================
-- ROLLBACK SCRIPT
-- ============================================================================
-- DROP TABLE IF EXISTS report_post;