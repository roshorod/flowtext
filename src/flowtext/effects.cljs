(ns ^:fighweel-always flowtext.effects
  (:require [cljs.core.async :refer [chan put! dropping-buffer]]
            [flowtext.input :refer [deselect]]))

(def selection-ch (chan (dropping-buffer 1)))
(def selection-args-ch (chan (dropping-buffer 1)))

(defmulti input (fn [_ _ params] (:action params)))

(defmethod input :select [_ _ args]
  (put! selection-args-ch args)
  (put! selection-ch :select))
