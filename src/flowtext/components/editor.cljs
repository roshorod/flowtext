(ns ^:fighweel-always flowtext.components.editor
  (:require [flowtext.components.line :refer [line]]
            [flowtext.input :refer [insert event-ch]]
            [flowtext.mixins.selection :as selection]
            [cljs.core.async :refer [put!]]
            [citrus.core :as citrus]
            [rum.core :as r]))

(r/defc editor <
  selection/handle
  r/reactive
  [r]
  (let [state (r/react (citrus/subscription r [:lines]))]
    [:div#editor
     {:content-editable                  true
      :suppress-content-editable-warning true
      :on-key-down #(do (.persist %)
                        (.preventDefault %)
                        (put! event-ch %))}
     (->> state
          (mapv #(-> (line %)
                     (r/with-key (:id %)))))]))
