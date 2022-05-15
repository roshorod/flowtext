(ns ^:fighweel-always flowtext.com.editor
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [flowtext.input.spec :as spec]
            [flowtext.subs :as subs]
            [flowtext.events :as event]
            [flowtext.com.line :as l]))

(def EditorHandler
  (fn [event]
    (let [key (.-key event)]
      (.preventDefault event)
      (cond
        (spec/text? key)
        (do (.preventDefault event)
            (rf/dispatch [::event/token-insert-char key]))
        
        (spec/backspace? key)
        (do (.preventDefault event)
            (rf/dispatch [::event/token-delete-char]))))))

(def EditorSelection
  (fn []
    (rf/dispatch-sync [::event/cursor-select])))

(defn EditorWrapper [fun]
  (r/create-class
    {:display-name         ""
     :component-did-update EditorSelection
     :reagent-render       fun}))

(defn Editor [lines]
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
         @lines)])))
