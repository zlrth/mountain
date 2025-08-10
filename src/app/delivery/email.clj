(ns app.delivery.email
  (:require [org.httpkit.client :as http]
            [cheshire.core :as json]))

(defn send-digest! [{:keys [token from stream]} {:keys [to subject html text]}]
  (let [payload {:From from :To to :Subject subject :HtmlBody html :TextBody text :MessageStream (or stream "outbound")}
        {:keys [status body]} @(http/post "https://api.postmarkapp.com/email"
                                          {:headers {"Accept" "application/json"
                                                     "Content-Type" "application/json"
                                                     "X-Postmark-Server-Token" token}
                                           :body (json/generate-string payload)})]
    {:status status :body (when body (json/parse-string body true))}))
