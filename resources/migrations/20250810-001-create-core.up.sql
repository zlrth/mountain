-- 20250810-001-create-core.up.sql
-- Core tables (MVP)
CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE IF NOT EXISTS app_user (
  id BIGSERIAL PRIMARY KEY,
  email CITEXT UNIQUE NOT NULL,
  tz TEXT NOT NULL DEFAULT 'America/New_York',
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS feed (
  id BIGSERIAL PRIMARY KEY,
  url TEXT UNIQUE NOT NULL,
  title TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS post (
  id BIGSERIAL PRIMARY KEY,
  feed_id BIGINT NOT NULL REFERENCES feed(id),
  dedupe_key TEXT NOT NULL,
  guid TEXT,
  canonical_url TEXT,
  title TEXT,
  author TEXT,
  published_at TIMESTAMPTZ,
  updated_at TIMESTAMPTZ,
  summary TEXT,
  content_html TEXT,
  content_text TEXT,
  content_hash TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(feed_id, dedupe_key)
);
CREATE INDEX IF NOT EXISTS post_feed_published_idx ON post(feed_id, published_at);

-- read state
CREATE TABLE IF NOT EXISTS post_read (
  user_id BIGINT NOT NULL REFERENCES app_user(id),
  post_id BIGINT NOT NULL REFERENCES post(id) ON DELETE CASCADE,
  read_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  PRIMARY KEY(user_id, post_id)
);
