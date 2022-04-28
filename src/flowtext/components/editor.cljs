(ns ^:fighweel-always flowtext.components.editor
  (:require [flowtext.components.line :refer [line]]
            [flowtext.components.number :refer [number]]
            [flowtext.input.handler :refer [handle]]
            [flowtext.mixins.selection :as selection]
            [cljs.core.async :refer [put!]]
            [citrus.core :as citrus]
            [rum.core :as r]))

(r/defc editor <
  selection/handle
  r/reactive
  [r]
  (let [state (r/react (citrus/subscription r [:lines]))]
    [:div.editor-wrapper
     [:div.line-number-wrapper
      (map
        (fn [line]
          (-> (number (:id line))
              (r/with-key (:id line))))
        state)]
     [:div#editor
      {:content-editable                  true
       :suppress-content-editable-warning true
       :spell-check                       false
       :on-key-down                       #(handle % r)}
      [:div.line-wrapper
       (->> state
            (mapv #(-> (line %)
                       (r/with-key (:id %)))))]]]))
