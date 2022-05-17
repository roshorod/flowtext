(ns ^:fighweel-always flowtext.token.events
  (:require [flowtext.common.normalized :as normed]
            [flowtext.token.subs :as token-subs]
            [flowtext.cursor.subs :as cursor-subs]
            [flowtext.cursor.events :as cursor-event]
            [flowtext.line.events :as line-event]
            [flowtext.token.core :as core]
            [flowtext.input.spec :as spec]
            [re-frame.core :as rf]))

(def token-interceptors
  "Get token from selection"
  [(rf/inject-cofx ::cursor-subs/selection-info)
   (token-subs/get-token-from ::token-subs/selection)])

(def token-from-interceptors
  "Get all tokens(id) passed to interceptor"
  [rf/trim-v
   (rf/inject-cofx ::cursor-subs/selection-info)
   (token-subs/get-token-from ::token-subs/interceptor)])

(rf/reg-event-db
  ::token-update
  (fn [db [_ info token-updated]]
    (let [{:keys [line token] } info]
      (assoc-in db [:lines line :tokens token] token-updated))))

(rf/reg-event-db
  ::token-delete
  (fn [db [_ info]]
    (let [line (get-in db [:lines (:line info) :tokens])]
      (assoc-in
        db
        [:lines (:line info) :tokens]
        (normed/normalize-tokens (dissoc line (:token info)))))))

(rf/reg-event-fx
  ::token-concat
  token-from-interceptors
  (fn [{:keys [tokens selection] } [x y]]
    (let [line       (:line selection)
          token      (:token selection)
          token-new  (reduce core/token-concat tokens)
          token-info {:line   line
                      :token  (dec token)
                      :offset (count (:content (first tokens)))}]
      {:dispatch-n [[::token-update token-info token-new]
                    [::token-delete selection]
                    [::cursor-event/cursor-update token-info]]})))

(rf/reg-event-fx
  ::token-insert-char
  token-interceptors
  (fn [{:keys [token selection] :as coeffect } [_ char]]
    (let [{:keys [offset] } selection]
      {:fx [[:dispatch [::token-update selection
                        (core/token-insert token char offset)]]
            [:dispatch [::cursor-event/cursor-update
                        (-> selection
                            (assoc :offset (inc offset)))]]]})))

(rf/reg-event-fx
  ::token-delete-char
  (conj token-interceptors
        [(rf/inject-cofx ::token-subs/has-next-token?)
         (rf/inject-cofx ::token-subs/prev-token-info)])
  (fn [{:keys [token selection has-next-token? prev-token-info]} _]
    (let [token-id (:token selection)
          line-id  (:line selection)
          offset   (:offset selection)]
      
      (cond
        (spec/wrap-back? offset line-id token-id)
        {:dispatch [::line-event/line-wrap-back line-id (dec line-id)]}

        (spec/token-start? offset)
        (if (spec/can-concat? line-id token-id)
          {:dispatch [::token-concat (dec token-id) token-id]}
          (prn "first line do nothing"))
        
        (spec/token-body? offset)
        (let [token (core/token-remove token offset)]
          (if (empty? (:content token))
            
            (if (not= token-id 0)
              {:dispatch-n [[::token-delete selection]
                            [::cursor-event/cursor-update
                             (-> selection
                                 (assoc :token (dec token-id)))]]}
              
              (if has-next-token?
                {:dispatch-n [[::token-delete selection] ; delete token
                              [::cursor-event/cursor-update
                               (-> selection
                                   (assoc :token (dec token-id)))]]}
                
                (if (or (not= line-id 0)
                        (not= token-id 0))
                  {:dispatch-n [[::line-delete line-id]
                                [::cursor-event/cursor-update prev-token-info]]}
                  
                  (prn "Last char on editor. Do something for last char"))))
            
            {:dispatch-n [[::token-update selection token]
                          [::cursor-event/cursor-update
                           (-> selection
                               (assoc :offset (dec offset)))]]}))))))
