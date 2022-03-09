(ns ^:figwheel-always store.action
  (:require [store.core :refer [action!]]))

(defmethod action! :nav/toggle [state [action data]]
  (update state [:navbar :open?] not))
