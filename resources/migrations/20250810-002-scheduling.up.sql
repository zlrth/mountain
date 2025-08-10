-- 20250810-002-scheduling.up.sql
CREATE TABLE IF NOT EXISTS subscription (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES app_user(id),
  feed_id BIGINT NOT NULL REFERENCES feed(id),
  UNIQUE(user_id, feed_id)
);

CREATE TABLE IF NOT EXISTS schedule (
  id BIGSERIAL PRIMARY KEY,
  subscription_id BIGINT NOT NULL REFERENCES subscription(id) ON DELETE CASCADE,
  mode TEXT NOT NULL CHECK (mode IN ('finish_by_date','cadence')),
  target_date DATE,
  cadence_n INTEGER,
  cadence_unit TEXT,
  weekdays BOOLEAN[7],
  send_time TIME NOT NULL,
  tz TEXT NOT NULL,
  paused BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS delivery (
  id BIGSERIAL PRIMARY KEY,
  schedule_id BIGINT NOT NULL REFERENCES schedule(id) ON DELETE CASCADE,
  run_at_utc TIMESTAMPTZ NOT NULL,
  planned_count INTEGER,
  status TEXT NOT NULL DEFAULT 'queued',
  error TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS delivery_post (
  delivery_id BIGINT NOT NULL REFERENCES delivery(id) ON DELETE CASCADE,
  post_id BIGINT NOT NULL REFERENCES post(id) ON DELETE CASCADE,
  position INTEGER NOT NULL,
  PRIMARY KEY(delivery_id, post_id)
);
