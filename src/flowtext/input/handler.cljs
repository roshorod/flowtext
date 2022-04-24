(ns ^:figwheel-always flowtext.input.handler
  (:require [flowtext.input.spec :as spec]
            [flowtext.input.next :as next]
            [flowtext.input.prev :as prev]
            [flowtext.input.backspace :refer [backspace]]
            [flowtext.input.whitespace :refer [whitespace]]
            [flowtext.input.enter :refer [enter]]
            [flowtext.input.insert :refer [insert]]))

(defn handle [^js/Event event reconciler]
  (let [key (.-key event)]
    (.preventDefault event)
    (try
      (cond
        (spec/right? key)
        (next/offset)

        (spec/left? key)
        (prev/offset)
        
        (spec/text? key)
        (insert reconciler key)

        (spec/backspace? key)
        (backspace reconciler)

        (spec/enter? key)
        (enter reconciler)
        
        (spec/space? key)
        (whitespace reconciler))
      (catch :default e
        (case (ex-cause e)
          :next/token (next/token)
          :prev/token (prev/token)
          :node-error (js/console.error e))))))
