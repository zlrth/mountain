(ns ui.views
  (:require [re-frame.core :as rf]))

(defn add-feed-card []
  (let [url @(rf/subscribe [:feed/url])
        adding? @(rf/subscribe [:feed/adding?])
        res @(rf/subscribe [:feed/last-result])]
    [:div.card
     [:h2 "Add a Feed"]
     [:div
      [:label "Feed URL"]
      [:input {:type "url" :placeholder "https://slatestarcodex.com/feed/"
               :value url :on-change #(rf/dispatch [:feed/url (.. % -target -value)])}]]
     [:div {:style {:height "10px"}}]
     [:button {:disabled (or adding? (empty? url))
               :on-click #(rf/dispatch [:feed/add])}
      (if adding? "Adding..." "Add feed")]
     (when res
       [:div {:style {:margin-top "10px"}}
        [:div "Response: " [:code (pr-str res)]]])]))

(defn weekday-picker []
  [:div.muted "Weekday subsets will come in a later UI pass. (Backend supports it.)"])

(defn schedule-card []
  (let [sid @(rf/subscribe [:sched/subscription-id])
        mode @(rf/subscribe [:sched/mode])
        n @(rf/subscribe [:sched/cadence-n])
        unit @(rf/subscribe [:sched/cadence-unit])
        send-time @(rf/subscribe [:sched/send-time])
        tz @(rf/subscribe [:sched/tz])]
    [:div.card
     [:h2 "Create Schedule"]
     [:div.row
      [:div {:style {:flex "1"}}
       [:label "Subscription ID"]
       [:input {:type "text" :value sid :on-change #(rf/dispatch [:sched/subscription-id (.. % -target -value)])}]]
      [:div {:style {:flex "1"}}]]
     [:div.row
      [:div {:style {:flex "1"}} 
       [:label "Mode"]
       [:select {:value mode :on-change #(rf/dispatch [:sched/mode (.. % -target -value)])}
        [:option {:value "cadence"} "Cadence (N per interval)"]
        [:option {:value "finish_by_date"} "Finish by date (basic stub)"]]]
      [:div {:style {:flex "1"}}
       [:label "Cadence N"]
       [:input {:type "number" :min 1 :value n
                :on-change #(rf/dispatch [:sched/cadence-n (.. % -target -value)])}]]
      [:div {:style {:flex "1"}}
       [:label "Cadence Unit"]
       [:select {:value unit :on-change #(rf/dispatch [:sched/cadence-unit (.. % -target -value)])}
        [:option {:value "day"} "day"]
        [:option {:value "week"} "week"]
        [:option {:value "month"} "month"]]]]
     [:div.row
      [:div {:style {:flex "1"}}
       [:label "Send time (HH:MM, local to TZ)"]
       [:input {:type "time" :value send-time :on-change #(rf/dispatch [:sched/send-time (.. % -target -value)])}]]
      [:div {:style {:flex "1"}}
       [:label "Timezone"]
       [:input {:type "text" :value tz :on-change #(rf/dispatch [:sched/tz (.. % -target -value)])}]]]
     [weekday-picker]
     [:div {:style {:height "10px"}}]
     [:button {:on-click #(rf/dispatch [:sched/create])} "Create schedule"]]))

(defn log-card []
  (let [lines @(rf/subscribe [:log])]
    [:div.card
     [:h2 "Log"]
     (if (seq lines)
       [:pre (for [l lines] ^{:key l} [:div l])]
       [:div.muted "No events yet."]) ]))
