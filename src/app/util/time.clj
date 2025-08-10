(ns app.util.time
  (:import (java.time ZonedDateTime ZoneId DayOfWeek)))

(defn day-of-week [^java.time.Instant inst tz]
  (-> (ZonedDateTime/ofInstant inst (ZoneId/of tz)) .getDayOfWeek (.getValue)))
