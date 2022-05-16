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
      (assoc cofx :selection (get-token-info selection)))))

;; `TODO:` delete when ref
(defn count-tokens [line]
  (count (seq (:tokens line))))

(rf/reg-cofx
  ::prev-token-info
  ;; First token return {:token -1 :line -1 :offset 0}
  (fn [{:keys [db selection] :as cofx}]
    (let [token (:token selection)
          line  (:line selection)]
      (assoc
        cofx :prev-token-info
        (if (<= token 0)
          (let [prev-line   (dec line)
                prev-tokens (dec (count-tokens (get-in db [:lines prev-line])))
                prev-token  (get-in db [:lines prev-line :tokens prev-tokens])
                prev-offset (count (:content prev-token))]
            {:token  prev-tokens
             :line   prev-line
             :offset prev-offset})
          (let [prev-token-id (dec token)
                prev-token    (get-in db [:lines line :tokens prev-token-id])
                prev-offset   (count (:content prev-token))]
            {:token  prev-token-id
             :line   line
             :offset prev-offset}))))))

(rf/reg-cofx
  ::has-next-token?
  (fn [{:keys [db selection] :as cofx}]
    (let [token      (:token selection)
          line       (:line selection)
          next-token (get-in db [:lines line :tokens (inc token)])]
      (assoc cofx :has-next-token? (some? next-token)))))
