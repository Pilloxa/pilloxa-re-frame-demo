(ns ^:figwheel-hooks pilloxa-re-frame-demo.core
  (:require [reagent.core :as reagent]
            [re-frame.core :refer [dispatch
                                   dispatch-sync
                                   reg-event-db
                                   reg-sub
                                   subscribe]]
            [cljsjs.react-chartjs-2]))

(def line-chart
  (reagent/adapt-react-class js/ReactChartjs2.Line))

(def app-db
  {:date-range ["2019-06-01" "2019-06-02" "2019-06-03" "2019-06-04"]
   :patients   {:patient-1 {:id           :patient-1
                            :dose-history [(rand-int 100) (rand-int 100) (rand-int 100) (rand-int 100)]
                            :color        "rgb(255, 0, 0)"
                            :selected?    true}
                :patient-2 {:id           :patient-2
                            :dose-history [92 93 95 95]
                            :color        "rgb(0, 0, 255)"
                            :selected?    true}}})

(reg-event-db
  :initialize
  (fn [_ _]
    app-db))

(reg-event-db
  :toggle-select
  (fn [db [_ patient-id]]
    (update-in db [:patients patient-id :selected?] not)))

(reg-sub
  :patients
  (fn [db _]
    (vals (:patients db))))

(reg-sub
  :selected-patients
  :<- [:patients]
  (fn [patients]
    (filter :selected? patients)))

(reg-sub
  :date-range
  (fn [db _]
    (:date-range db)))

(defn patient-selector
  "Component selects a single patient"
  [{:keys [id color selected?]}]
  [:div {:style {:display        :flex
                 :flex-direction :row
                 :align-items    :center
                 :margin-top     8}}
   [:div {:style {:width            8
                  :height           30
                  :background-color color}}]
   [:input {:style    {:margin 5}
            :type     :checkbox
            :checked  selected?
            :onChange #(dispatch [:toggle-select id])}]
   [:div {:style {:font-family "Arial"
                  :font-weight :lighter}}
    id]])

(defn patient-selectors
  "Component that selects patients"
  []
  (let [patients (subscribe [:patients])]
    [:div {:style {:display         :flex
                   :flex-direction  :column
                   :justify-content :space-around
                   :margin-right    50}}
     [:div {:style {:display        :flex
                    :flex-direction :column}}
      (for [patient @patients]
        ^{:key (:id patient)}
        [patient-selector patient])]]))

(defn dose-chart
  "Chart showing the doses"
  []
  (let [date-range        (subscribe [:date-range])
        selected-patients (subscribe [:selected-patients])]
    [:div {:style {:width 500}}
     [line-chart
      {:legend {:display false}
       :data   {:labels   @date-range
                :datasets (for [{:keys [id color dose-history]} @selected-patients]
                            {:label           id
                             :fill            false
                             :lineTension     0.1
                             :backgroundColor color
                             :borderColor     color
                             :data            dose-history})}}]]))

(defn app-root []
  [:div {:style {:display         :flex
                 :justify-content :space-around
                 :padding-top     50}}
   [:div {:style {:display     :flex
                  :align-items :center}}
    [patient-selectors]
    [dose-chart]]])

(defonce reload-cnt
  (atom 0))


(defn main []
  (when (zero? @reload-cnt)
    (dispatch-sync [:initialize]))
  (reagent/render [app-root] (js/document.getElementById "app")))

(defn ^:after-load on-reload []
  (swap! reload-cnt inc))

(main)
