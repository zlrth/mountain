(ns app.http
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str]
    [reitit.ring :as ring]
    [reitit.ring.middleware.parameters :as params]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [muuntaja.core :as m]
    [ring.util.http-response :as resp]        ;; for JSON helpers (ok, etc.)
    [ring.util.response :as rresp]            ;; for file-response
    [app.db :as db]
    [app.ingest.cdx :as cdx]
    [app.ingest.snapshot :as snap]
    [app.ingest.normalize :as norm]
    [next.jdbc :as jdbc]))

(defn ^:private mime-type [path]
  (cond
    (str/ends-with? path ".html") "text/html; charset=utf-8"
    (str/ends-with? path ".js")   "application/javascript; charset=utf-8"
    (str/ends-with? path ".css")  "text/css; charset=utf-8"
    (str/ends-with? path ".map")  "application/json; charset=utf-8"
    :else "application/octet-stream"))

(defn ^:private serve-public
  "Serve a file from the ./public directory. Returns 404 if not found."
  [rel-path]
  (let [file (io/file "public" rel-path)]
    (if (and (.exists file) (.isFile file))
      (-> (rresp/file-response (.getPath file))
          (rresp/content-type (mime-type rel-path)))
      (resp/not-found {:error (str "Not found: " rel-path)}))))

(defn handler-fn [{:keys [ds conf]}]
  (let [muunt (m/create (assoc m/default-options :default-format "application/json"))]
    (ring/ring-handler
      (ring/router
        [;; ---------- Static UI ----------
         ["/"           {:get (fn [_] (serve-public "index.html"))}]
         ["/index.html" {:get (fn [_] (serve-public "index.html"))}]
         ["/js/*path"   {:get (fn [{:keys [path-params]}]
                                (serve-public (str "js/" (:path path-params))))}]

         ;; ---------- JSON APIs ----------
         ["/api/feeds"
          {:post (fn [{:keys [body-params]}]
                   (let [url   (:url body-params)
                         id    (db/ensure-feed! ds url)
                         snaps (take 20 (cdx/list-snapshots url))]
                     (doseq [s snaps
                             :let [xml (snap/fetch-snapshot-xml s)]
                             :when xml
                             item (snap/parse-feed xml)]
                       (db/upsert-post! ds
                         (-> item
                             (assoc :feed_id id
                                    :dedupe_key (norm/dedupe-key item)))))
                     (resp/ok {:feed_id id :snapshots (count snaps)})))}]

         ["/api/schedules"
          {:post (fn [{:keys [body-params]}]
                   (let [row (jdbc/execute-one!
                               ds
                               ["INSERT INTO schedule (subscription_id,mode,cadence_n,cadence_unit,send_time,tz)
                                 VALUES (?,?,?,?,?,?) RETURNING id"
                                (:subscription_id body-params)
                                (:mode body-params)
                                (:cadence_n body-params)
                                (:cadence_unit body-params)
                                (:send_time body-params)
                                (:tz body-params)])]
                     (resp/ok row)))}]

         ["/api/postmark/webhook"
          {:post (fn [_] (resp/ok {:ok true}))}]]
        {:data {:muuntaja muunt
                :middleware [params/parameters-middleware
                             muuntaja/format-middleware]}})
      (ring/create-default-handler))))

(def handler (handler-fn {}))
