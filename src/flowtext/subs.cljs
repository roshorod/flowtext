(ns ^:figwheel-hooks flowtext.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  ::lines
  (fn [db _]
    (:lines db)))

(rf/reg-sub
  ::line-by-id
  (fn [db [_ id]]
    (get-in db [:lines id])))

(rf/reg-sub
  ::offset
  (fn [db _]
    (:offset db)))

(rf/reg-sub
  ::token
  (fn [db _]
    (:token db)))

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

(defn get-token-id [selection]
  (-> selection
      .-focusNode
      .-parentElement
      (.getAttribute "token")
      js/parseInt))

(defn get-line-id [selection]
  (-> selection
      .-focusNode
      .-parentElement
      .-parentElement
      (.getAttribute "line")
      js/parseInt))

(defn get-offset [selection]
  (-> selection
      .-focusOffset))

(defn get-token-info [selection]
  {:line   (get-line-id selection)
   :token  (get-token-id selection)
   :offset (get-offset selection)})

(rf/reg-cofx
  ::selection-info
  (fn [cofx]
    (let [selection (js/getSelection)]
      (assoc cofx :selection-info (get-token-info selection)))))
