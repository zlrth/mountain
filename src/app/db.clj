(ns app.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.connection :as connection]
            [next.jdbc.sql :as sql])
  (:import (com.zaxxer.hikari HikariDataSource)))

(defn datasource [jdbc-url]
  (connection/->pool HikariDataSource
    {:jdbcUrl jdbc-url
     :maximumPoolSize 10
     :autoCommit true}))

(defn ensure-feed! [ds url]
  (sql/insert! ds :feed {:url url} {:on-conflict [:url] :do-nothing true})
  (-> (jdbc/execute-one! ds ["SELECT id FROM feed WHERE url = ?" url]) :id))

(defn upsert-post! [ds m]
  (sql/insert! ds :post m
               {:on-conflict [:feed_id :dedupe_key]
                :do-update-set [:title :author :published_at :updated_at
                                :summary :content_html :content_text :content_hash :canonical_url]}))

(defn mark-read! [ds user-id post-id]
  (sql/insert! ds :post_read {:user_id user-id :post_id post-id}
               {:on-conflict [:user_id :post_id] :do-nothing true}))
