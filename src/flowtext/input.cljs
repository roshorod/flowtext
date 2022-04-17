(ns ^:figwheel-always flowtext.input
  (:require [cljs.core.async :refer-macros [go-loop]]
            [cljs.core.async :refer [<! chan]]
            [flowtext.utils :refer [char->content
                                    node->text]]
            [flowtext.spec :as spec]
            [citrus.core :as citrus]))

(def line-attrib "line")
(def token-attrib "token")

(def selection (js/getSelection))

(def event-ch (chan))

(defn- token? []
  (let [node (.-focusNode selection)]
    (try (.-parentElement node)
         (catch :default _
           (throw
             (ex-info "Canno't find react node." {}
                      :node-error))))))

(defn select
  ([offset]
   (select offset (node->text (token?))))
  ([offset node]
   (let [range (.createRange js/document)]
     (.collapse range false)
     (.selectNode range node)
     (.setStart range node offset)
     (.setEnd range node offset)
     (.removeAllRanges selection)
     (.addRange selection range))))

(defn deselect []
  (.removeAllRanges selection))

(defn insert [r char]
  (let [node    (token?)
        offset  (-> selection
                    .-focusOffset)
        content (.-innerText node)
        token   (-> (.getAttribute node token-attrib)
                    js/parseInt)
        line    (-> node
                    .-parentElement
                    (.getAttribute line-attrib)
                    js/parseInt)]
    (citrus/dispatch!
      r
      :lines :token/update
      {:line-id  line
       :token-id token
       :node     (node->text node)
       :offset   (inc offset)
       :content  (char->content content char offset)})))

(defn whitespace [r]
  (let [node          (token?)
        offset        (-> selection
                          .-focusOffset)
        content       (.-innerText node)
        line          (-> node
                          .-parentElement
                          (.getAttribute line-attrib)
                          js/parseInt)
        length        (count content)
        left-token    (-> (.getAttribute node token-attrib)
                          js/parseInt)
        right-token   (inc left-token)
        left-content  (subs content 0 offset)
        right-content (str " " (subs content offset length))]
    (citrus/dispatch!
      r
      :lines :line/append
      {:line-id  line
       :token-id left-token
       :map      (-> []
                     (conj
                       {:id left-token :content left-content})
                     (conj
                       {:id right-token :content right-content}))
       :offset   1
       :node     (-> node .-nextSibling node->text)})))

(defn next-offset []
  (let [offset (.-focusOffset selection)]
    (try (select (inc offset))
         (catch :default _
           (throw (ex-info "End of token." {} :next/token))))))

(defn prev-offset []
  (let [offset (.-focusOffset selection)]
    (try (select (dec offset))
         (catch :default _
           (throw (ex-info "Start of token." {} :prev/token))))))

(defn next-token []
  (let [node (-> (token?) .-nextSibling)]
    (select 1 (node->text node))))

(defn prev-token []
  (let [node   (-> (token?) .-previousSibling)
        offset (-> node .-firstChild .-length dec)]
    (select offset (node->text node))))

(defn handle [reconciler]
  (go-loop []
    (let [event (<! event-ch)
          key   (.-key event)]
      (try (cond
             (spec/right? key)
             (next-offset)

             (spec/left? key)
             (prev-offset)
             
             (spec/text? key)
             (insert reconciler key)

             (spec/space? key)
             (whitespace reconciler))
           (catch :default e
             (case (ex-cause e)
               :next/token (next-token)
               :prev/token (prev-token))))
      (recur))))
