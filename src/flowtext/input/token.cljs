(ns ^:figwheel-always flowtext.input.token
  (:require [flowtext.input.selection :as selection]))

(def line-attribute "line")
(def token-attribute "token")

(defn- token!-
  "Get selected node of text"
  []
  (let [node (.-focusNode selection/get)]
    (try
      (.-parentElement node)
      (catch :default _
        (throw
          (ex-info "Canno't find node." {} :node-error))))))

(defn- line-
  "Get id of token line"
  [^js/HTMLElement token!]
  (-> token!
      .-parentElement
      (.getAttribute line-attribute)
      js/parseInt))

(defn- token-
  "Get id of token"
  [^js/HTMLElement token!]
  (-> token!
      (.getAttribute token-attribute)
      js/parseInt))

(defn- content-
  "Get content of token"
  [^js/HTMLElement token!]
  (-> token!
      .-innerText))

(deftype ^:private Token [])

(deftype ^:private Next- [node]
  IDeref
  (-deref [_]
    (-> node .-nextSibling)))

(deftype ^:private Prev- [node]
  IDeref
  (-deref [_]
    (-> node .-previousSibling)))

(extend-type Token
  IDeref
  (-deref [_]
    (let [node (token!-)]
      {:node    node
       :line    (line- node)
       :token   (token- node)
       :content (content- node)
       :offset  (selection/offset)
       :next    (->Next- node)
       :prev    (->Prev- node)})))

(def token! (->Token))
