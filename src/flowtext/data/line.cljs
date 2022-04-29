(ns ^:fighweel-always flowtext.data.line
  (:require [flowtext.data.node :as n]))

(def attribute "line")

(defrecord Line [id tokens])

(defn line [id tokens]
  (map->Line {:id id :tokens tokens}))

(defn node->token-id [node]
  (-> node
      .-parentElement
      (.getAttribute attribute)
      js/parseInt))

(defn node->next-line [node]
  (-> node
      .-parentElement
      .-nextSibling))

(defn line->last-token [line]
  (.-lastChild line))

(defn node->prev-line-last-token-offset [node]
  (-> (n/node->prev-line node)
      .-lastChild
      .-firstChild
      .-length
      js/parseInt))
