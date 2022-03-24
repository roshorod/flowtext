(ns ^:figwheel-hooks flowtext.core
  (:require [components.editor :refer [editor]]
            [store.core :as store]
            [rum.core :as r]))

(enable-console-print!)
(store/enable-state-print!)

(defn ^:after-load re-render []
  (r/mount
    (editor)
    (.getElementById js/document "root")))

(defonce start-up (re-render))
