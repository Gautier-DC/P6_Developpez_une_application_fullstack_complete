-- MDD Database Schema
-- This script initializes the database schema for the MDD application

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Themes table
CREATE TABLE IF NOT EXISTS themes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Articles table
CREATE TABLE IF NOT EXISTS articles (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id),
    theme_id BIGINT NOT NULL REFERENCES themes(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Comments table
CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    article_id BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User subscriptions to themes
CREATE TABLE IF NOT EXISTS subscriptions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    theme_id BIGINT NOT NULL REFERENCES themes(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, theme_id)
);

-- Indexes for better performance
CREATE INDEX IF NOT EXISTS idx_articles_user_id ON articles(user_id);
CREATE INDEX IF NOT EXISTS idx_articles_theme_id ON articles(theme_id);
CREATE INDEX IF NOT EXISTS idx_comments_article_id ON comments(article_id);
CREATE INDEX IF NOT EXISTS idx_comments_user_id ON comments(user_id);
CREATE INDEX IF NOT EXISTS idx_subscriptions_user_id ON subscriptions(user_id);
CREATE INDEX IF NOT EXISTS idx_subscriptions_theme_id ON subscriptions(theme_id);

-- Insert sample themes
INSERT INTO themes (name, description) VALUES
    ('JavaScript', 'Discussions sur JavaScript, ES6+, frameworks et bonnes pratiques'),
    ('Java', 'Tout sur Java, Spring Boot, JPA et l''écosystème Java'),
    ('Angular', 'Framework Angular, TypeScript, RxJS et développement frontend'),
    ('PostgreSQL', 'Base de données PostgreSQL, optimisation et requêtes avancées'),
    ('DevOps', 'Docker, CI/CD, déploiement et infrastructure')
ON CONFLICT DO NOTHING;

-- Insert sample users (password is 'Test!1234' hashed with BCrypt)
INSERT INTO users (email, username, password) VALUES
    ('alice.dev@example.com', 'alice_dev', '$2a$10$CwTycUXWue0Thq9StjUM0uLczlDrWzUqf5SYFxRhFXb.VhbXGyfT2'),
    ('bob.coder@example.com', 'bob_coder', '$2a$10$CwTycUXWue0Thq9StjUM0uLczlDrWzUqf5SYFxRhFXb.VhbXGyfT2'),
    ('charlie.tech@example.com', 'charlie_tech', '$2a$10$CwTycUXWue0Thq9StjUM0uLczlDrWzUqf5SYFxRhFXb.VhbXGyfT2'),
    ('diana.frontend@example.com', 'diana_frontend', '$2a$10$CwTycUXWue0Thq9StjUM0uLczlDrWzUqf5SYFxRhFXb.VhbXGyfT2'),
    ('eve.backend@example.com', 'eve_backend', '$2a$10$CwTycUXWue0Thq9StjUM0uLczlDrWzUqf5SYFxRhFXb.VhbXGyfT2')
ON CONFLICT DO NOTHING;

-- Insert sample articles with lorem ipsum content
INSERT INTO articles (title, content, user_id, theme_id, created_at, updated_at) VALUES
    ('Introduction au JavaScript moderne',
     'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris.',
     1, 1, '2024-09-01 10:30:00', '2024-09-01 10:30:00'),

    ('Spring Boot : Guide complet',
     'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium.',
     2, 2, '2024-09-05 14:15:00', '2024-09-05 14:15:00'),

    ('Angular Signals expliqués',
     'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris vel lorem at nunc tempor facilisis. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae.',
     3, 3, '2024-09-10 09:45:00', '2024-09-10 09:45:00'),

    ('Optimisation PostgreSQL',
     'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit.',
     4, 4, '2024-09-12 16:20:00', '2024-09-12 16:20:00'),

    ('Docker pour les débutants',
     'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas.',
     5, 5, '2024-09-15 11:00:00', '2024-09-15 11:00:00'),

    ('Les nouvelles fonctionnalités ES2023',
     'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.',
     1, 1, '2024-09-18 13:30:00', '2024-09-18 13:30:00'),

    ('Microservices avec Spring Cloud',
     'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.',
     2, 2, '2024-09-22 08:45:00', '2024-09-22 08:45:00'),

    ('Déploiement CI/CD avec GitHub Actions',
     'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus lacinia odio vitae vestibulum. Donec auctor blandit quam, ac sollicitudin urna molestie id. Curabitur blandit tempus porttitor. Nullam quis risus eget urna mollis ornare vel eu leo.',
     5, 5, '2024-09-25 15:10:00', '2024-09-25 15:10:00')
ON CONFLICT DO NOTHING;