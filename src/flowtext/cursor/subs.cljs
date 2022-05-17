(ns ^:fighweel-always flowtext.cursor.subs
  (:require [flowtext.cursor.core :as core]
            [re-frame.core :as rf]))

(rf/reg-sub
  ::token
  (fn [db _]
    (:token db)))

(rf/reg-sub
  ::offset
  (fn [db _]
    (:offset db)))

(rf/reg-sub
  ::line
  (fn [db _]
    (:line db)))

(rf/reg-sub
  ::cursor
  :<- [::line]
  :<- [::token]
  :<- [::offset]
  (fn [[line token offset] _]
    [line token offset]))

(rf/reg-cofx
  ::selection-info
  (fn [cofx]
    (let [selection (js/getSelection)]
      (assoc cofx :selection (core/get-token-info selection)))))
