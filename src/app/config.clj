(ns app.config)

(defn env [k & [default]]
  (or (System/getenv k) default))

(defn cfg []
  {:db {:jdbc-url (env "MOUNTAIN_DATABASE_URL" "jdbc:postgresql://localhost:5432/mountain?user=postgres")}
   :postmark {:token (env "MOUNTAIN_POSTMARK_SERVER_TOKEN")
              :from  (env "MOUNTAIN_POSTMARK_FROM" "no-reply@example.com")
              :stream (env "MOUNTAIN_POSTMARK_STREAM" "outbound")}
   :app {:base-url (env "MOUNTAIN_BASE_URL" "http://localhost:3000")
         :rate-limit-rps (Integer/parseInt (env "MOUNTAIN_WAYBACK_RPS" "1"))}})
