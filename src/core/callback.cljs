(ns ^:figwheel-always core.callback
  (:require [core.state :refer [reg-callback]]))

(reg-callback
  :initial-state
  (fn [state initial-state]
    (reset! state initial-state)))

(reg-callback
  :token-update
  (fn [state {:keys [line-id token-id content]}]
    (let [lines   (:lines @state)
          token   (get-in lines [line-id :tokens token-id])
          updated (-> token
                      (assoc :content content))]
      (swap! state assoc-in [:lines line-id :tokens token-id]
             updated))))
