(ns app.scheduler.engine
  (:require [jarohen.chime :as chime]
            [clojure.core.async :as a]
            [app.util.time :as t]))

(defn eligible? [weekday-set inst tz]
  (contains? weekday-set (t/day-of-week inst tz)))

(defn start-ticker! [tick-chan]
  (chime/chime-at (chime/periodic-seq (java.time.Instant/now) (java.time.Duration/ofMinutes 1))
                  (fn [_] (a/put! tick-chan :tick))))
