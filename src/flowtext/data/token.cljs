(ns ^:fighweel-always flowtext.data.token
  (:refer-clojure :exclude [map])
  (:require [cljs.spec.alpha :as s]
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
