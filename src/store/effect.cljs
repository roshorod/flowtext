(ns ^:figwheel-always store.effect
  (:require [store.core :refer [effect!]]))

(defmethod effect! :nav/toggle
  [[old-state new-state] [action data]]
  nil)

