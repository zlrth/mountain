(ns ui.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [ui.events]
            [ui.subs]
            [ui.views :as views]))

(defn root []
  [:div
   [views/add-feed-card]
   [views/schedule-card]
   [views/log-card]])

(defn mount []
  (rf/clear-subscription-cache!)
  (r/render [root] (.getElementById js/document "app")))

(defn ^:export init []
  (rf/dispatch-sync [:initialize])
  (mount))
