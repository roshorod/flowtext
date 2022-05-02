(ns ^:figwheel-hooks flowtext.events
  (:refer-clojure :exclude [key])
  (:require [re-frame.core :as rf]
            [flowtext.subs :as subs]))

(def initial-lines
  [{:tokens [{:content "first"}
             {:content "second"}
             {:content "third"}]}
   {:tokens [{:content "first"}
             {:content "second"}
             {:content "third"}]}
   {:tokens []}
   {:tokens [{:content "first"}
             {:content "second"}
             {:content "third"}]}
   {:tokens [{:content "first"}
             {:content "second"}
             {:content "third"}]}])

(def initial-state
  {:lines  initial-lines
   :line   0
   :token  0
   :offset 0})

;; :coeffects - current state of data. Which enter in event handler.
;; :effects - state of data returned by event handler.

(rf/reg-event-db
  ::cursor-update
  (fn
    [db [_ {:keys [offset token line]}]]
    (-> db
        (assoc :offset offset)
        (assoc :token token)
        (assoc :line line))))

(rf/reg-event-fx
  ::cursor-select
  (fn
    [{:keys [db]} _]
    (let [offset    (:offset db)
          range     (.createRange js/document)
          selection (js/getSelection)
          node      (.-focusNode selection)]
      (.collapse range false)
      (.selectNode range node)
      (.setStart range node offset)
      (.setEnd range node offset)
      (.removeAllRanges selection)
      (.addRange selection range))))

(def get-token-in
  (fn [coeffects]
    (let [lines (get-in coeffects [:db :lines])
          info  (get-in coeffects [:selection-info])
          token (get-in lines [(:line info) :tokens (:token info)])]
      (assoc coeffects :token token))))

(def get-token
  (rf/->interceptor
    :id ::get-token-interceptor
    :before (fn [ctx]
              (let [fn- get-token-in]
                (update-in ctx [:coeffects] fn-)))))

(def token-interceptors
  [(rf/inject-cofx ::subs/selection-info) get-token])

(rf/reg-event-db
  ::token-update
  (fn
    [db [_ info token-updated]]
    (let [{:keys [line token]} info]
      (assoc-in db [:lines line :tokens token] token-updated))))

(defn- insert-char [string char idx]
  (str (subs string 0 idx) char (subs string idx)))

(defn token-insert [{:keys [content] :as token} key offset]
  (-> token
      (assoc :content (insert-char content key offset))))

(rf/reg-event-fx
  ::token-insert-char
  token-interceptors
  (fn
    [{:keys [token selection-info]} [_ key]]
    (let [{:keys [offset]} selection-info]
      {:fx [[:dispatch [::token-update selection-info
                        (token-insert token key offset)]]
            [:dispatch [::cursor-update
                        (-> selection-info
                            (assoc :offset (inc offset)))]]]})))

(rf/reg-event-db
  ::init-state
  (fn
    [_ _]
    initial-state))
