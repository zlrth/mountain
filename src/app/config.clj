(ns app.config)

(defn env [k & [default]]
  (or (System/getenv k) default))

(defn cfg []
  {:db {:jdbc-url (env "DATABASE_URL" "jdbc:postgresql://localhost:5432/wayback_drip?user=postgres&password=postgres")}
   :postmark {:token (env "POSTMARK_SERVER_TOKEN")
              :from  (env "POSTMARK_FROM" "no-reply@example.com")
              :stream (env "POSTMARK_STREAM" "outbound")}
   :app {:base-url (env "BASE_URL" "http://localhost:3000")
         :rate-limit-rps (Integer/parseInt (env "WAYBACK_RPS" "1"))}})
