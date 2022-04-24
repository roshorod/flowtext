(ns ^:figwheel-always flowtext.input.select
  (:require [flowtext.input.utils :as utils]
            [flowtext.input.token :as token]
            [flowtext.input.selection :as selection]))

(defn select
  ([offset]
   (let [{:keys [node]} @token/token!]
     (select offset (utils/node->text node))))
  ([offset node]
   (let [range (.createRange js/document)]
     (.collapse range false)
     (.selectNode range node)
     (.setStart range node offset)
     (.setEnd range node offset)
     (.removeAllRanges selection/get)
     (.addRange selection/get range))))

(defn deselect []
  (.removeAllRanges selection/get))
