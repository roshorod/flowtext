(ns ^:fighweel-always flowtext.mixins.selection
  (:require [cljs.core.async :refer [poll! <!]]
            [cljs.core.async :refer-macros [go]]
            [flowtext.input :as input]
            [flowtext.effects :refer [selection-ch
                                      selection-args-ch]]))

(def handle
  {:did-update
   (fn [state]
     (input/deselect)
     (when-let [action (poll! selection-ch)]
       (go (let [{:keys [offset node]} (<! selection-args-ch)]
             (case action
               :select (input/select offset node)))))
     state)})
