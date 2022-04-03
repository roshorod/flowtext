(ns ^:fighweel-always components.editor
  (:require [rum.core :as r]
            [cljs.core.async :refer [put!]]
            [components.line :refer [line]]
            [core.state :refer [subscribe]]
            [core.input :refer [input event-ch]]))

(r/defc editor < r/reactive []
  (let [lines (subscribe :lines)]
    [:div#editor
     {:content-editable                  true
      :suppress-content-editable-warning true
      :on-key-down #(do (.persist %)
                        (.preventDefault %)
                        (put! event-ch %))}
     (->> lines
          (mapv #(-> (line %)
                     (r/with-key (:id %)))))]))
