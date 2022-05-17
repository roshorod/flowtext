(ns ^:figwheel-hooks flowtext.core
  (:require [reagent.dom :as rd]
            [reagent.core :as re]
            [re-frame.core :as rf]
            [flowtext.line.events :as line]
            [flowtext.cursor.events :as cursor]
            [flowtext.views :as views]))

(enable-console-print!)

(defn ^:after-load re-render []
  (rf/clear-subscription-cache!)
  (rd/render
    [views/Editor]
    (.getElementById js/document "root")))

(rf/reg-event-fx
  ::initialize
  (fn [_ _]
    {:dispatch-n
     [[::line/initialize-db]
      [::cursor/initialize-db]]}))

(defonce start-up
  (do (rf/dispatch-sync [::initialize])
      (re-render)))
