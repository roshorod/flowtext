(ns ^:figwheel-always flowtext.utils
  (:refer-clojure :exclude [map]))

(defn char->content [string char index]
  (str (subs string 0 index) char (subs string index)))

(defn node->text [node] (.-firstChild node))

(defn assoc-tokens-index [map]
  (->> map
       (keep-indexed (fn [index token]
                       (assoc token :id index)))))

(defn assoc-token-map [tokens map index]
  (let [[left rigth] (split-at index tokens)]
    (into [] (-> []
                 (concat left)
                 (concat map)
                 (concat rigth)
                 assoc-tokens-index))))
