(ns ui.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub :feed/url (fn [db _] (:feed/url db)))
(rf/reg-sub :feed/adding? (fn [db _] (:feed/adding? db)))
(rf/reg-sub :feed/last-result (fn [db _] (:feed/last-result db)))

(rf/reg-sub :sched/subscription-id (fn [db _] (:sched/subscription-id db)))
(rf/reg-sub :sched/mode (fn [db _] (:sched/mode db)))
(rf/reg-sub :sched/cadence-n (fn [db _] (:sched/cadence-n db)))
(rf/reg-sub :sched/cadence-unit (fn [db _] (:sched/cadence-unit db)))
(rf/reg-sub :sched/send-time (fn [db _] (:sched/send-time db)))
(rf/reg-sub :sched/tz (fn [db _] (:sched/tz db)))

(rf/reg-sub :log (fn [db _] (:log db)))
