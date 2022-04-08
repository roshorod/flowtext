(ns ^:figwheel-always core.callback
  (:refer-clojure :exclude [map])
  (:require [core.state :refer [reg-callback]]
            [core.token :refer [append-token-map]]))

(reg-callback
  :initial-state
  (fn [state initial-state]
    (reset! state initial-state)))

(reg-callback
  :token-append-map
  (fn [state  {:keys [line-id token-id map]}]
    (let [tokens (->> (get-in (:lines @state) [line-id :tokens])
                      (filterv #(not= token-id (:id %))))]
      (swap!
        state assoc-in [:lines line-id :tokens]
        (append-token-map tokens map token-id)))))

(reg-callback
  :token-update
  (fn [state {:keys [line-id token-id content]}]
    (let [lines   (:lines @state)
          token   (get-in lines [line-id :tokens token-id])
          updated (-> token
                      (assoc :content content))]
      (swap!
        state assoc-in [:lines line-id :tokens token-id]
        updated))))

