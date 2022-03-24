(ns ^:fighweel-always components.editor
  (:require [rum.core :as r])
  (:refer-clojure :exclude [range]))

(def caret-node (atom nil))
(def caret-offset (atom nil))

(def selection (atom (js/window.getSelection)))

(defn caret [selection node offset]
  (let [range (.createRange js/document)]
    (.setStart range node offset)
    (.setEnd range node offset)
    (.removeAllRanges selection)
    (.addRange selection range)))

(def handle-input
  (fn [event]
    (.preventDefault event)
    (let [key (.-key event)]
      (swap! caret-node #(.-focusNode @selection))
      (swap! caret-offset #(.-focusOffset @selection))
      
      (try
        (cond
          (= key "ArrowRight")
          (try 
            (caret @selection @caret-node (inc @caret-offset))
            (catch :default _
              (throw (ex-info "End of token. Next token" {} :next-subling))))
          (= key "ArrowLeft")
          (try 
            (caret @selection @caret-node (dec @caret-offset))
            (catch :default _
              (throw (ex-info "Start of token. Previous token" {} :prev-subling))))
          )
        (catch ExceptionInfo e
          (case (ex-cause e)
            :next-subling
            (let [next-node (.-nextSibling
                              (.-parentElement @caret-node))
                  next-text (.-firstChild next-node)]
              (swap! caret-node #(identity next-text))
              (swap! caret-offset #(identity 0))
              (caret @selection @caret-node (inc @caret-offset)))
            :prev-subling
            (let [prev-node  (.-previousSibling
                               (.-parentElement @caret-node))
                  prev-text  (.-firstChild prev-node)
                  end-offset (.-length prev-text)]
              (swap! caret-node #(identity prev-text))
              (swap! caret-offset #(identity end-offset))
              (caret @selection @caret-node (dec @caret-offset)))))))))

(def handle-mouse-up
  (fn [event]
    (swap! caret-offset #(.-focusOffset @selection))))

(r/defc line [lines]
  [:pre
   lines])

(r/defc token [content]
  [:span
   content])

(r/defc editor < r/reactive []
  [:div#editor
   {:content-editable                  true
    :suppress-content-editable-warning true
    :on-key-down                       handle-input
    :on-mouse-up                       handle-mouse-up}
   (line
     [(token "first ")
      (token "second ")
      (token "third ")])])
