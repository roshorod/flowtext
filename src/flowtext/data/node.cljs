(ns ^:fighweel-always flowtext.data.node
  (:require [flowtext.data.token :as t]))

(deftype ^:private Next [node]
  IDeref
  (-deref [_]
    (-> node .-nextSibling)))

(deftype ^:private Prev [node]
  IDeref
  (-deref [_]
    (-> node .-previousSibling)))

(deftype ^:private Parent [node]
  IDeref
  (-deref [_]
    (-> node .-parentElement)))

(defn get-node [selection]
  (let [node (-> selection
                 .-focusNode
                 .-parentElement)]
    {:node   node
     :text   (.-firstChild node)
     :offset (.-focusOffset selection)
     :parent (Parent. node)
     :next   (Next. node)
     :prev   (Prev. node)}))

(defn node->token-id [node]
  (-> node (.getAttribute t/attribute)))

(defn node->text [node]
  (-> node .-firstChild))

(defn node->text-length [node]
  (.-length (node->text node)))
