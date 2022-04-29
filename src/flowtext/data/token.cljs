(ns ^:fighweel-always flowtext.data.token
  (:refer-clojure :exclude [map])
  (:require [flowtext.data.node :as n]
            [cljs.spec.alpha :as s]
            [cljs.spec.test.alpha :as st]))

(def attribute "token")

(defrecord Token [id content])

(defn token [id content]
  (map->Token {:id id :content content}))

(defn node->token [node]
  (let [id      (-> node (.getAttribute attribute)
                    js/parseInt)
        content (-> node .-innerText)]
    (->Token id content)))

(defn node->token-id [node]
  (-> node
      (.getAttribute attribute)
      js/parseInt))

(defn find-token-by-id [line id]
  (loop [node (.-firstChild line)]
    (let [token-id (node->token-id node)]
      (prn token-id)
      (if (= id token-id)
        node
        (recur (.-nextSibling node))))))

(defn find-last-token-id-of-prev-line [node]
  (-> (n/node->prev-line node)
      .-lastChild 
      (.getAttribute attribute)
      js/parseInt))
