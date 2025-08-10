(ns app.core
  (:require [integrant.core :as ig]
            [app.config :as config]
            [app.db :as db]
            [app.http :as http]
            [org.httpkit.server :as httpkit]))

(defmethod ig/init-key :db/datasource [_ {:keys [jdbc-url]}]
  (db/datasource jdbc-url))

(defmethod ig/halt-key! :db/datasource [_ ds]
  (some-> ds (.close)))

(defmethod ig/init-key :http/server [_ {:keys [port ds conf]}]
  (let [handler (http/handler-fn {:ds ds :conf conf})
        stop-fn (httpkit/run-server handler {:port (or port 3000)})]
    {:stop stop-fn}))

(defmethod ig/halt-key! :http/server [_ {:keys [stop]}]
  (when stop (stop)))

(defn system-map []
  (let [{:keys [db postmark app]} (config/cfg)]
    {:db/datasource {:jdbc-url (:jdbc-url db)}
     :http/server   {:port 3000 :ds (ig/ref :db/datasource) :conf {:postmark postmark :app app}}}))

(defonce ^:private system* (atom nil))

(defn start! []
  (let [sys (ig/init (system-map))]
    (reset! system* sys)
    (println "[app.core] System started.")
    :started))

(defn stop!
  []
  (when-let [sys @system*]
    (println "[app.core] Halting system…")
    (ig/halt! sys)
    (reset! system* nil)
    (println "[app.core] System stopped.")
    :stopped))

(defn -main
  [& _]
  (stop!)
  (start!))
