(ns ^:fighweel-always flowtext.components.editor
  (:require [flowtext.components.line :refer [line]]
            [flowtext.mixins.selection :as selection]
            [flowtext.input.handler :refer [handle]]
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
      :on-key-down                       #(handle % r)}
     (->> state
          (mapv #(-> (line %)
                     (r/with-key (:id %)))))]))
