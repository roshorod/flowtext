(ns ^:fighweel-always flowtext.effects
  (:require [cljs.core.async :refer [chan put!]]
            [flowtext.input :refer [deselect]]))

(def selection-ch (chan))
(def selection-args-ch (chan))

(defmulti input (fn [_ _ params] (:action params)))

(defmethod input :select [_ _ args]
  (put! selection-args-ch args)
  (put! selection-ch :select))
