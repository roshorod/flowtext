(ns ^:figwheel-hooks flowtext.core
  (:require [reagent.dom :as rd]
            [reagent.core :as re]
            [re-frame.core :as rf]
            [flowtext.events :as event]
            [flowtext.com.layout :refer [Layout]]))

(enable-console-print!)

(defn ^:after-load re-render []
  (rf/clear-subscription-cache!)
  (rd/render
    [Layout]
    (.getElementById js/document "root")))

(defonce start-up
  (do (rf/dispatch-sync [::event/init-state])
      (re-render)))
