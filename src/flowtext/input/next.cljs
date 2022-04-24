(ns ^:figwheel-always flowtext.input.next
  (:refer-clojure :exclude [next])
  (:require [flowtext.input.token :as token]
            [flowtext.input.utils :as utils]
            [flowtext.input.selection :as selection]
            [flowtext.input.select :refer [select]]))

(defn offset []
  (let [offset (selection/offset)]
    (try
      (select (inc offset))
      (catch :default _
        (throw (ex-info "End of token." {} :next/token))))))

(defn token []
  (let [{:keys [next]} @token/token!]
    (try 
      (select 1 (utils/node->text @next))
      (catch :default _
        (throw (ex-info "End of line." {} :next/line))))))
