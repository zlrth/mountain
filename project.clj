(defproject mountain "0.1.0-SNAPSHOT"
  :description "RSS→Wayback backfill + scheduled email drip"
  :min-lein-version "2.9.8"
  :dependencies [[org.clojure/clojure "1.12.1"]
                 [metosin/reitit "0.9.1"]
                 [http-kit "2.8.0"]
                 [com.github.seancorfield/next.jdbc "1.3.1048"]
                 [com.zaxxer/HikariCP "7.0.1"]
                 [migratus "1.6.4"]
                 [integrant "0.13.1"]
                 [integrant/repl "0.4.0"]
                 [com.rometools/rome "2.1.0"]
                 [org.jsoup/jsoup "1.21.1"]
                 [cheshire "6.0.0"]
                 [jarohen/chime "0.3.3"]
                 [org.clojure/core.async "1.8.741"]
                 [clj-commons/clj-yaml "1.0.29"]
                 [org.postgresql/postgresql "42.7.7"]
                 [metosin/muuntaja "0.6.11"]
                 [metosin/ring-http-response "0.9.5"]]
  :plugins [[lein-ring "0.12.6"] [migratus-lein "0.7.3"]]
  :ring {:handler app.http/handler}
  :migratus {:store :database
             :migration-dir "migrations"
             :db {:jdbc-url #=(eval (or (System/getenv "DATABASE_URL")
                                        "jdbc:postgresql://localhost:5432/wayback_drip?user=postgres&password=postgres"))}}
  :profiles {:dev {:dependencies [[hawk "0.2.11"]]
                   :source-paths ["dev"]}})
