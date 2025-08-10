(ns app.ingest.cdx
  (:require [org.httpkit.client :as http]
            [cheshire.core :as json]))

(defn list-snapshots
  "Return seq of {:ts \"yyyyMMddHHmmss\" :url <original>} for the given feed URL."
  [feed-url]
  (let [q {:url feed-url :output "json" :fl "timestamp,original" :gzip "false" :filter "statuscode:200"}
        url "https://web.archive.org/cdx/search/cdx"
        {:keys [status body]} @(http/get url {:query-params q})]
    (when (= 200 status)
      (let [rows (json/parse-string body)]
        (->> rows (drop 1) (map (fn [[ts orig]] {:ts ts :url orig})))))))

(defn snapshot-url [{:keys [ts url]}]
  (str "https://web.archive.org/web/" ts "/" url))
