(ns ^:figwheel-always flowtext.input.handler
  (:require [flowtext.input.spec :as spec]
            [flowtext.input.next :as next]
            [flowtext.input.prev :as prev]
            [flowtext.input.backspace :refer [backspace]]
            [flowtext.input.whitespace :refer [whitespace]]
            [flowtext.input.enter :refer [enter]]
            [flowtext.input.insert :refer [insert]]
            [citrus.core :as c]))

(defn handle [^js/Event event r]
  (let [key (.-key event)]
    (.preventDefault event)
    (try
      (cond
        (spec/right? key)
        (c/dispatch! r :select :next/offset)

        (spec/left? key)
        (c/dispatch! r :select :prev/offset)
        
        
        (spec/text? key)
        (insert r key)

        (spec/backspace? key)
        (backspace r)

        (spec/enter? key)
        (enter r)
        
        (spec/space? key)
        (whitespace r))
      (catch :default e
        (case (ex-cause e)
          :next/token (next/token)
          :prev/token (prev/token)
          :node-error (js/console.error e))))))
