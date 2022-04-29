(ns ^:fighweel-always flowtext.effects
  (:require [cljs.core.async :refer [chan put!]]
            [citrus.core :as citrus]))

(def selection-ch (chan))
(def selection-args-ch (chan))

(defmulti input (fn [_ _ params] (:action params)))

(defmethod input :select [_ _ args]
  (put! selection-ch :select)
  (put! selection-args-ch args))

(defn dispatch [r c {:keys [control action args]}]
  (citrus/dispatch! r control action args))
