(ns ^:figwheel-always flowtext.input.utils
  (:refer-clojure :exclude [map])
  (:require [flowtext.input.token :as token]))

(defn valid-offset [offset]
  (if (pos-int? offset)
    offset
    0))

(defn char->content [string char index]
  (str (subs string 0 index) char (subs string index)))

(defn content->remove
  "Remove from content a char by offset."
  [content offset]
  (let [length (count content)]
    (str (subs content 0 (dec offset))
         (subs content offset length))))

(defn node->token-id [node]
  (-> node
      (.getAttribute token/token-attribute)
      js/parseInt))

(defn node->text [node] (.-firstChild node))

(defn node->length [node]
  (.-length (node->text node)))

(defn node->prev-line [node]
  (let [line (.-parentElement node)]
    (.-previousSibling line)))

(defn node->next-line [node]
  (let [line (.-parentElement node)]
    (.-nextSibling line)))

(defn node->prev-line-offset [node]
  (count (.-textContent (node->prev-line node))))

(defn line->last-token [line]
  (.-lastChild line))

(defn line->first-token [line]
  (.-firstChild line))

(defn assoc-tokens-index [map]
  (->> map
       (keep-indexed (fn [index token]
                       (assoc token :id index)))
       vec))

(defn assoc-token-map [tokens map index]
  (let [[left rigth] (split-at index tokens)]
    (into [] (-> []
                 (concat left)
                 (concat map)
                 (concat rigth)
                 assoc-tokens-index))))
