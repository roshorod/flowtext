(ns ^:fighweel-always flowtext.controllers.select
  (:refer-clojure :exclude [next])
  (:require [flowtext.input.select :as s]
            [flowtext.data.token :as t]
            [flowtext.data.node :as n]))

(defmulti control (fn [event] event))

(defmethod control :init [_ _ state]
  {:state (js/getSelection)})

(defmethod control :next/offset [_ _ state]
  (let [node    (n/get-node state)
        content (:text node)
        offset  (inc (:offset node))]
    (if (<= offset (.-length content))
      (s/select offset content)
      {:dispatch {:control :line :action :next/token}})))

(defmethod control :prev/offset [_ _ state]
  (let [node    (n/get-node state)
        content (:text node)
        offset  (dec (:offset node))]
    (if (>= offset 0)
      (s/select offset content)
      {:dispatch {:control :line :action :prev/token}})))

(defmethod control :next/token [_ _ state]
  (let [node (n/get-node state)
        next @(:next node)]
    (if (nil? next)
      (prn "Next line")
      (s/select 1 (n/node->text next)))))

(defmethod control :prev/token [_ _ state]
  (let [node (n/get-node state)
        prev @(:prev node)]
    (if (nil? prev)
      (prn "Prev line")
      (s/select (dec (n/node->text-length prev))
                (n/node->text prev)))))
