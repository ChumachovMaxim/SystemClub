CREATE TABLE studios (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         name VARCHAR(255) NOT NULL UNIQUE,  -- ← УНИКАЛЬНОЕ ИМЯ
                         description TEXT,
                         owner_id UUID NOT NULL,
                         followers_count BIGINT NOT NULL DEFAULT 0,
                         avatar_url VARCHAR(512),
                         created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Индекс для поиска студий по владельцу
CREATE INDEX idx_studios_owner_id ON studios(owner_id);

-- Уникальный индекс по имени (для быстрых проверок и читаемости)
CREATE UNIQUE INDEX uix_studios_name ON studios(LOWER(name));

-- Таблица админов студии
CREATE TABLE studio_admins (
                               studio_id UUID NOT NULL,
                               admin_id UUID NOT NULL,
                               PRIMARY KEY (studio_id, admin_id),
                               FOREIGN KEY (studio_id) REFERENCES studios(id) ON DELETE CASCADE
);

CREATE INDEX idx_studio_admins_studio_id ON studio_admins(studio_id);

-- Подписчики
CREATE TABLE studio_followers (
                                  id BIGSERIAL PRIMARY KEY,
                                  studio_id UUID NOT NULL,
                                  user_id UUID NOT NULL,
                                  UNIQUE (studio_id, user_id),
                                  FOREIGN KEY (studio_id) REFERENCES studios(id) ON DELETE CASCADE
);

CREATE INDEX idx_studio_followers_studio_id ON studio_followers(studio_id);
CREATE INDEX idx_studio_followers_user_id ON studio_followers(user_id);  -- ← полезно для "мои подписки"

-- Посты
CREATE TABLE posts (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       studio_id UUID NOT NULL,
                       content TEXT NOT NULL,
                       author_id UUID NOT NULL,
                       timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       attachment_url VARCHAR(512),
                       FOREIGN KEY (studio_id) REFERENCES studios(id) ON DELETE CASCADE
);

CREATE INDEX idx_posts_studio_id ON posts(studio_id);
CREATE INDEX idx_posts_timestamp ON posts(timestamp DESC);  -- ← для сортировки по дате

-- Пользователи (существование в системе)
CREATE TABLE studio_users (
                              user_id UUID PRIMARY KEY
);
