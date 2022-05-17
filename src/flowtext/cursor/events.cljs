(ns ^:fighweel-always flowtext.cursor.events
  (:require [flowtext.cursor.db :as cursor]
            [flowtext.cursor.core :as core]
            [re-frame.core :as rf]))

(rf/reg-event-db
  ::initialize-db
  (fn [db _]
    (merge db cursor/default-db)))

(rf/reg-event-fx
  ::cursor-select
  (fn
    [{:keys [db]} _]
    (cljs.pprint/pprint db)
    (let [offset    (:offset db)
          line      (:line db)
          token     (:token db)
          node      (.-firstChild
                      (core/token-selector line token))
          range     (.createRange js/document)
          selection (js/getSelection)]
      (.collapse range true)
      (.selectNode range node)
      (.setStart range node offset)
      (.setEnd range node offset)
      (.removeAllRanges selection)
      (.addRange selection range))))

(rf/reg-event-db
  ::cursor-update
  (fn
    [db [_ {:keys [offset token line]}]]
    (-> db
        (assoc :offset offset)
        (assoc :token token)
        (assoc :line line))))
