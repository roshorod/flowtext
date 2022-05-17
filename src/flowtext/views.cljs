(ns ^:figwheel-always flowtext.views
  (:require [flowtext.token.events :as token]
            [flowtext.cursor.events :as cursor]
            [flowtext.line.views :as l]
            [flowtext.line.subs :as line]
            [flowtext.input.spec :as spec]
            [re-frame.core :as rf]
            [reagent.core :as r]))

(def EditorHandler
  (fn [event]
    (let [key (.-key event)]
      (.preventDefault event)
      (cond
        (spec/text? key)
        (do (.preventDefault event)
            (rf/dispatch [::token/token-insert-char key]))
        
        (spec/backspace? key)
        (do (.preventDefault event)
            (rf/dispatch [::token/token-delete-char]))))))

(def EditorSelection
  (fn []
    (rf/dispatch-sync [::cursor/cursor-select])))

(defn EditorWrapper [fun]
  (r/create-class
    {:display-name         ""
     :component-did-update EditorSelection
     :reagent-render       fun}))

(defn Editor []
  (let [lines (rf/subscribe [::line/lines])]
    (EditorWrapper
      (fn []
        [:div.editor-wrapper
         {:content-editable                  true
          :spell-check                       false
          :suppress-content-editable-warning true
          :on-key-down                       EditorHandler}
         (map-indexed
           (fn [idx line]
             ^{:key idx}
             [l/Line idx (reduce into (rest line))])
           @lines)]))))
