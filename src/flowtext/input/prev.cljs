(ns ^:figwheel-always flowtext.input.prev
  (:require [flowtext.input.token :as token]
            [flowtext.input.utils :as utils]
            [flowtext.input.selection :as selection]
            [flowtext.input.select :refer [select]]))

(defn offset []
  (let [offset (selection/offset)]
    (try
      (select (dec offset))
      (catch :default _
        (throw (ex-info "Start of token." {} :prev/token))))))

(defn token []
  (let [{:keys [prev node]} @token/token!]
    (try 
      (let [prev    @prev
            offset  (dec (utils/node->length prev))
            content (utils/node->text prev)]
        (select offset content))
      (catch :default _
        (throw (ex-info "Start of line." {} :prev/line))))))
