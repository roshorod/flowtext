(ns ^:figwheel-hooks flowtext.core
  (:require [flowtext.components.editor :refer [editor]]
            [flowtext.controllers.line :as lines]
            [flowtext.controllers.select :as select]
            [flowtext.effects :as effects]
            [citrus.core :as citrus]
            [rum.core :as r]))

(enable-console-print!)

(defonce reconciler
  (citrus/reconciler
    {:state           (atom {})
     :controllers     {:lines  lines/control
                       :select select/control}
     :effect-handlers {:input    effects/input
                       :dispatch effects/dispatch}}))

(defonce init-controllers
  (citrus/broadcast! reconciler :init))

(defn ^:after-load re-render []
  (r/mount
    (editor reconciler)
    (.getElementById js/document "root")))

(defonce start-up (re-render))
