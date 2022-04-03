(ns ^:figwheel-hooks core.core
  (:require [components.editor :refer [editor]]
            [core.state :refer [dispatch]]
            [core.input :refer [input]]
            [core.callback]
            [rum.core :as r]))

(enable-console-print!)

(defn ^:after-load re-render []
  (r/mount
    (editor)
    (.getElementById js/document "root"))
  (input :handle))

(defonce start-up (re-render))

(def initial-lines
  [{:id 0 :tokens [{:id 0 :content "first "}
                   {:id 1 :content "second "}
                   {:id 2 :content "third "}]}
   {:id 1 :tokens [{:id 0 :content "first "}
                   {:id 1 :content "second "}
                   {:id 2 :content "third "}]}
   {:id 2 :tokens [{:id 0 :content "first "}
                   {:id 1 :content "second "}
                   {:id 2 :content "third "}]}
   {:id 3 :tokens [{:id 0 :content "first "}
                   {:id 1 :content "second "}
                   {:id 2 :content "third "}]}])

(def initial-state
  {:lines initial-lines})

(defonce start-up-state
  (dispatch :initial-state initial-state))
