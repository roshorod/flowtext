(ns ^:fighweel-always flowtext.line.events
  (:require [flowtext.line.subs :as line-subs]
            [flowtext.line.db :as line]
            [flowtext.token.core :as token]
            [flowtext.cursor.db :as cursor]
            [flowtext.cursor.subs :as cursor-subs]
            [flowtext.cursor.events :as cursor-event]
            [flowtext.common.normalized :as normed]
            [re-frame.core :as rf]))

(def line-from-interceptors
  "Get all lines(id) passed to interceptor"
  [rf/trim-v
   (rf/inject-cofx ::cursor-subs/selection-info)
   (line-subs/get-line-from ::line-subs/interceptor)])

(rf/reg-event-db
  ::initialize-db
  (fn [db _]
    (assoc db :lines (normed/normalize-lines line/default-db))))

(rf/reg-event-db
  ::line-update
  (fn [db [_ id line]]
    (assoc-in db [:lines id] line)))

(rf/reg-event-db
  ::line-delete
  (fn [db [_ id]]
    (let [lines (get-in db [:lines])]
      (assoc-in db [:lines] (normed/normalize-lines (dissoc lines id))))))

(rf/reg-event-fx
  ::line-wrap-back
  line-from-interceptors
  (fn [{:keys [lines selection]} [curr-line prev-line]]
    (let [curr        (first lines)
          prev        (first (next lines))
          prev-length (token/count-tokens prev)
          normalized  (normed/normalize-tokens-vec
                        (concat []
                                (:tokens prev)
                                (:tokens curr)))
          updated     (update curr :tokens merge normalized)
          cursor      (-> cursor/default-db
                          (assoc :line prev-line)
                          (assoc :token prev-length))]
      {:dispatch-n [[::line-update prev-line updated]
                    [::line-delete curr-line]
                    [::cursor-event/cursor-update cursor]]})))
