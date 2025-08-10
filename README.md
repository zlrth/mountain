# mountain (MVP)

End-to-end skeleton: add feed → backfill from Wayback (first 20 snapshots) → create schedule → (email sender + webhook stubs).

## Quickstart
1. Install: Java 17+, Leiningen, Postgres.
2. Create DB `Mountain` and export `DATABASE_URL` env var.
3. Run migrations:
   ```bash
   lein migratus init
   lein migratus migrate
   ```
4. Run app:
   ```bash
   POSTMARK_SERVER_TOKEN=... POSTMARK_FROM=you@example.com lein run
   ```
5. Seed a feed:
   ```bash
   curl -X POST http://localhost:3000/api/feeds -H 'Content-Type: application/json' -d '{"url":"https://slatestarcodex.com/feed/"}'
   ```

## Notes
- Scheduler tick + email batching are sketched; extend `app.scheduler.engine` and wire delivery selection.
- See `resources/migrations` for schema.
