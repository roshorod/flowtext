(ns ^:fighweel-always flowtext.token.subs
  (:require [flowtext.token.core :as core]
            [re-frame.core :as rf]))

(def get-token-selection
  (fn [coeffects]
    (let [lines (get-in coeffects [:db :lines])
          info  (get-in coeffects [:selection])
          token (get-in lines [(:line info) :tokens (:token info)])]
      (assoc coeffects :token token))))

(def get-token-interceptor
  (fn [coeffects]
    (let [tokens (get coeffects :event)
          info   (get coeffects :selection)
          lines  (get-in coeffects [:db :lines])]
      (-> coeffects
          (assoc :tokens
                 (mapv
                   (fn [token]
                     (get-in lines [(:line info) :tokens token]))
                   tokens))))))

(defn get-token-from [target]
  (rf/->interceptor
    :id ::get-token-from-interceptor
    :before (fn [ctx]
              (let [fn- (case target
                          ::selection   get-token-selection
                          ::interceptor get-token-interceptor)]
                (update-in ctx [:coeffects] fn-)))))

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
                prev-tokens (dec (core/count-tokens (get-in db [:lines prev-line])))
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
