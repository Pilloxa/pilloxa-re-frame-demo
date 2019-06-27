(ns ^:figwheel-hooks pilloxa-re-frame-demo.core
  (:require [reagent.core :as reagent]
            [cljsjs.react-chartjs-2]))

(def line-chart
  (reagent/adapt-react-class js/ReactChartjs2.Line))

(defonce app-db
  (reagent/atom
    [(rand-int 100) (rand-int 100) (rand-int 100) (rand-int 100)]))

(defn dose-chart
  "Chart showing the doses"
  []
  [:div {:style {:width 500}}
   [line-chart
    {:data {:labels (range (count @app-db))
            :datasets [{:data @app-db
                        :label "Test data"}]}}]])

(defn app-root []
  [dose-chart])

(defn main []
  (reagent/render [app-root] (js/document.getElementById "app")))

(main)
