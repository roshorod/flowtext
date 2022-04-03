(ns ^:figwheel-hooks core.core
  (:require [components.editor :refer [editor handle-input]]
            [core.state :refer [enable-state-print!]]
            [rum.core :as r]))

(enable-console-print!)

(defn ^:after-load re-render []
  (r/mount
    (editor)
    (.getElementById js/document "root")))

(defonce start-up (re-render))
