(ns ^:fighweel-always flowtext.mixins.selection
  (:require [cljs.core.async :refer [poll! <!]]
            [cljs.core.async :refer-macros [go]]
            [flowtext.input.select :refer [select deselect]]
            [flowtext.input.utils :as utils]
            [flowtext.effects :refer [selection-ch
                                      selection-args-ch]]))

(def handle
  {:did-update
   (fn [state]
     (deselect)
     (when-let [action (poll! selection-ch)]
       (go (let [{:keys [offset node]} (<! selection-args-ch)]
             (try 
               (case action
                 :select
                 (select
                   (utils/valid-offset offset)
                   (utils/node->text node)))
               (catch :default e
                 (js/console.error e))))))
     state)})
