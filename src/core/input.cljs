(ns ^:figwheel-always core.input
  (:require [cljs.core.async :refer-macros [go-loop]]
            [cljs.core.async :refer [chan <!]]
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
    (.addRange selection range)))

(defmethod input :handle [_]
  (go-loop []
    (let [event (<! event-ch)
          key   (.-key event)]
      (token :insert/char key))
    (recur)))
