(ns ^:fighweel-always flowtext.line.subs
  (:require [re-frame.core :as rf]))

(def get-line-interceptor
  (fn [coeffects]
    (let [args  (get coeffects :event)
          lines (get-in coeffects [:db :lines])]
      (-> coeffects
          (assoc :lines
                 (mapv
                   (fn [line]
                     (get-in lines [line]))
                   args))))))

(defn get-line-from [target]
  (rf/->interceptor
    :id ::get-line-from-interceptor
    :before (fn [ctx]
              (let [fn- (case target
                          ::interceptor get-line-interceptor)]
                (update-in ctx [:coeffects] fn-)))))

(rf/reg-sub
  ::lines
  (fn [db _]
    (:lines db)))

(rf/reg-sub
  ::line-by-id
  (fn [db [_ id]]
    (get-in db [:lines id])))
