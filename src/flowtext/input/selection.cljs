(ns ^:figwheel-always flowtext.input.selection
  (:refer-clojure :exclude [get]))

(def get (js/getSelection))

(defn offset []
  (-> get .-focusOffset))
