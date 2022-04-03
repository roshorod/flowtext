(ns ^:figwheel-always core.state
  (:require [rum.core :as r]))

(defonce ^:private state (atom {}))
(defonce ^:private callbacks (atom {}))

(defn reg-callback [name callback]
  (swap! callbacks update name #(conj (or % []) callback)))

(defn subscribe [name]
  (r/react (r/cursor state name)))

(defn dispatch [name & args]
  (doseq [callback (name @callbacks)]
    (apply callback (cons state args))))
