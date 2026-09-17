CREATE TABLE IF NOT EXISTS tracked_games (
    game_id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    thumb_url TEXT,
    lowest_price_usd TEXT,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS active_user_alerts (
    alert_id TEXT PRIMARY KEY,
    game_id TEXT NOT NULL,
    user_email TEXT NOT NULL,
    target_price_usd TEXT NOT NULL,
    status TEXT DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
