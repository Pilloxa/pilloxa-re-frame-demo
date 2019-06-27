(ns ^:figwheel-hooks pilloxa-re-frame-demo.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]))


(def initial-state
  {})

(rf/reg-event-db
  :initialize
  (fn [_ _]
    initial-state))

(defn app-root []
  [:div
   [:p "Hello"]])

(defn ^:export main []
  (reagent/render [app-root] (js/document.getElementById "app")))

(defn ^:after-load on-reload []
  (main))
