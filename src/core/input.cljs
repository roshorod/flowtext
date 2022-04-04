(ns ^:figwheel-always core.input
  (:require [cljs.core.async :refer-macros [go-loop]]
            [cljs.core.async :refer [chan <!]]
            [core.input-spec :refer [text? left? right?]]
            [core.token :refer [token]]))

(def event-ch (chan))

(defmulti input identity)

(defmethod input :select/token
  [_ selection node offset]
  (let [range (.createRange js/document)
        text  (.-firstChild node)]
    (.collapse range false)
    (.selectNode range text)
    (.setStart range text offset)
    (.setEnd range text offset)
    (.removeAllRanges selection)
    (.addRange selection range)))

(defmethod input :next/token-offset [_]
  (let [selection (js/getSelection)
        node      (token :selection/node)
        offset    (.-focusOffset selection)]
    (try
      (input :select/token selection node (inc offset))
      (catch :default _
        (throw (ex-info "End of token." {} :next/token))))))

(defmethod input :prev/token-offset [_]
  (let [selection (js/getSelection)
        node      (token :selection/node)
        offset    (.-focusOffset selection)]
    (try 
      (input :select/token selection node (dec offset))
      (catch :default _
        (throw (ex-info "Start of token." {} :prev/token))))))

(defmethod input :next/token [_]
  (let [selection (js/getSelection)
        node      (-> (token :selection/node)
                      .-nextSibling)]
    (input :select/token selection node 1)))

(defmethod input :prev/token [_]
  (let [selection (js/getSelection)
        node      (-> (token :selection/node)
                      .-previousSibling)
        offset    (-> node .-firstChild .-length)]
    (input :select/token selection node (dec offset))))

(defmethod input :handle [_]
  (go-loop []
    (let [event (<! event-ch)
          key   (.-key event)]
      (try
        (cond
          (right? key)
          (input :next/token-offset)
          
          (left? key)
          (input :prev/token-offset)
          
          (text? key)
          (token :insert/char key))
        (catch ExceptionInfo e
          (case (ex-cause e)
            :next/token
            (input :next/token)
            
            :prev/token
            (input :prev/token)))))
    (recur)))
