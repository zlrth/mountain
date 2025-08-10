(ns app.ingest.snapshot
  (:require [org.httpkit.client :as http]
            [app.ingest.cdx :as cdx]
            [app.ingest.normalize :as norm])
  (:import (com.rometools.rome.io SyndFeedInput XmlReader)))

(defn parse-feed [^String xml]
  (let [input (SyndFeedInput.)
        feed (.build input (XmlReader. (java.io.ByteArrayInputStream. (.getBytes xml "UTF-8"))))]
    (map (fn [^com.rometools.rome.feed.synd.SyndEntry e]
           {:title (.getTitle e)
            :link  (some-> e .getLink)
            :guid  (some-> e .getUri)
            :published_at (some-> e .getPublishedDate)
            :updated_at   (some-> e .getUpdatedDate)
            :summary (some-> e .getDescription .getValue)})
         (.getEntries feed))))

(defn fetch-snapshot-xml [snapshot]
  (let [url (cdx/snapshot-url snapshot)
        {:keys [status body]} @(http/get url {:headers {"Accept" "application/rss+xml, application/xml"}})]
    (when (= 200 status) body)))
