(ns ui.events
  (:require [re-frame.core :as rf]))

(rf/reg-event-db
 :initialize
 (fn [_ _]
   {:feed/url ""
    :feed/adding? false
    :feed/last-result nil
    :sched/subscription-id ""
    :sched/mode "cadence"
    :sched/cadence-n 2
    :sched/cadence-unit "day"
    :sched/send-time "08:00"
    :sched/tz "America/New_York"
    :log []}))

(defn log! [db msg]
  (update db :log conj (str (js/Date.) " — " msg)))

(rf/reg-event-db :feed/url (fn [db [_ v]] (assoc db :feed/url v)))

(defn ->json [m] (.stringify js/JSON (clj->js m)))

(defn fetch-json [url opts on-ok on-err]
  (-> (js/fetch url (clj->js opts))
      (.then (fn [resp]
               (if (.-ok resp)
                 (.json resp)
                 (throw (js/Error. (str "HTTP " (.-status resp)))))))
      (.then (fn [data] (on-ok (js->clj data :keywordize-keys true))))
      (.catch (fn [e] (on-err e)))))

(rf/reg-event-fx
 :feed/add
 (fn [{:keys [db]} _]
   (let [url (:feed/url db)]
     {:db (-> db
              (assoc :feed/adding? true)
              (log! (str "POST /api/feeds " url)))
      :dispatch-later [{:ms 10
                        :dispatch [:http/post
                                   {:url "/api/feeds"
                                    :body {:url url}
                                    :on-ok [:feed/add-ok]
                                    :on-err [:feed/add-err]}]}]})))

(rf/reg-event-fx
 :http/post
 (fn [{:keys [db]} [_ {:keys [url body on-ok on-err]}]]
   (fetch-json url {:method "POST"
                    :headers {"Content-Type" "application/json"}
                    :body (->json body)}
               (fn [data] (rf/dispatch (conj on-ok data)))
               (fn [e] (rf/dispatch (conj on-err (.-message e)))))
   {}))

(rf/reg-event-db
 :feed/add-ok
 (fn [db [_ data]]
   (-> db
       (assoc :feed/adding? false
              :feed/last-result data)
       (log! (str "Feed added. Response: " (pr-str data))))))

(rf/reg-event-db
 :feed/add-err
 (fn [db [_ msg]]
   (-> db
       (assoc :feed/adding? false)
       (log! (str "Feed add failed: " msg)))))

;; --- Schedule
(rf/reg-event-db :sched/subscription-id (fn [db [_ v]] (assoc db :sched/subscription-id v)))
(rf/reg-event-db :sched/mode (fn [db [_ v]] (assoc db :sched/mode v)))
(rf/reg-event-db :sched/cadence-n (fn [db [_ v]] (assoc db :sched/cadence-n (js/parseInt v))))
(rf/reg-event-db :sched/cadence-unit (fn [db [_ v]] (assoc db :sched/cadence-unit v)))
(rf/reg-event-db :sched/send-time (fn [db [_ v]] (assoc db :sched/send-time v)))
(rf/reg-event-db :sched/tz (fn [db [_ v]] (assoc db :sched/tz v)))

(rf/reg-event-fx
 :sched/create
 (fn [{:keys [db]} _]
   (let [{:keys [sched/subscription-id sched/mode sched/cadence-n sched/cadence-unit sched/send-time sched/tz]} db]
     {:dispatch [:http/post
                 {:url "/api/schedules"
                  :body {:subscription_id subscription-id
                         :mode mode
                         :cadence_n cadence-n
                         :cadence_unit cadence-unit
                         :send_time send-time
                         :tz tz}
                  :on-ok [:sched/ok]
                  :on-err [:sched/err]}]})))

(rf/reg-event-db :sched/ok (fn [db [_ data]] (log! db (str "Schedule created: " (pr-str data)))))
(rf/reg-event-db :sched/err (fn [db [_ msg]] (log! db (str "Schedule error: " msg))))
