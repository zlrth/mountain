(ns app.ingest.normalize
  (:import (java.net URI)
           (java.math BigInteger))
  (:require [clojure.string :as str]))

(defn strip-tracking [^String qs]
  (->> (str/split (or qs "") #"&")
       (keep (fn [kv]
               (let [[k v] (str/split kv #"=" 2)]
                 (when-not (re-matches #"(?i)utm_.*|fbclid|ref" k)
                   (str k (when v (str "=" v)))))))
       (str/join "&")))

(defn normalize-url [s]
  (let [u (URI. s)]
    (-> (URI. (.getScheme u)
              (.getUserInfo u)
              (some-> (.getHost u) str/lower-case)
              (.getPort u)
              (let [p (.getPath u)]
                (if (and p (.endsWith p "/")) (subs p 0 (dec (count p))) p))
              (let [q (strip-tracking (.getQuery u))] (when (seq q) q))
              nil)
        str)))

(defn sha256 [^String s]
  (let [md (java.security.MessageDigest/getInstance "SHA-256")]
    (format "%064x" (BigInteger. 1 (.digest md (.getBytes s "UTF-8"))))))

(defn dedupe-key [{:keys [guid link title published_at]}]
  (or (when (and guid (not (re-find #"(?i)random|uuid" guid))) guid)
      (some-> link normalize-url)
      (str (str/trim (str/lower-case (or title ""))) "|" (subs (or (str published_at) "") 0 10))))
