(ns ^:figwheel-hooks flowtext.events
  (:refer-clojure :exclude [key])
  (:require [re-frame.core :as rf]
            [flowtext.input.spec :as spec]
            [flowtext.subs :as subs]))

(defn normalize-sort [form]
  (into (sorted-map) form))

(defn normalize-index-map [form]
  (reduce
    into
    (map-indexed
      (fn [idx val]
        {idx (reduce into (next val))})
      form)))

(defn normalize-tokens [form]
  (-> form
      normalize-sort
      normalize-index-map))

(defn normalize-lines [form]
  (-> (map
        (fn [line]
          (let [tokens     (reduce into (next line))
                normalized (normalize-tokens
                             (get tokens :tokens))]
            {(first line) (assoc tokens :tokens normalized)}))
        form)
      normalize-sort
      normalize-index-map))


(def initial-lines
  {1 {:tokens {2 {:content "1"}, 1 {:content "2"}, 3 {:content "3"}}},
   0 {:tokens {2 {:content "first", :test "test"}, 1 {:content "second"}, 3 {:content "third"}}},
   4 {:tokens {3 {:content "first"}, 1 {:content "third"}}}
   3 {:tokens {1 {:content "One"}}}})

(def initial-state
  {:lines  (normalize-lines initial-lines)
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


(def line-property "line")
(def token-property "token")

(defn attribute-selector [name value]
  (str "[" name "=\"" value "\"]"))

(defn token-selector
  "Return node with token attribute.

  If not found return nil.
  
  `js/querySelectorAll` can be used with all
  css selectors like > even with pseudo-classes

  "
  [line token]
  (js/document.querySelector
    (str (attribute-selector line-property line)
         '>
         (attribute-selector token-property token))))

(def default-cursor
  {:token  0
   :line   0
   :offset 0})

(rf/reg-event-fx
  ::cursor-select
  (fn
    [{:keys [db]} _]
    (let [offset    (:offset db)
          line      (:line db)
          token     (:token db)
          node      (.-firstChild (token-selector line token))
          range     (.createRange js/document)
          selection (js/getSelection)]
      (.collapse range true)
      (.selectNode range node)
      (.setStart range node offset)
      (.setEnd range node offset)
      (.removeAllRanges selection)
      (.addRange selection range))))

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

(def token-interceptors
  "Get token from selection"
  [(rf/inject-cofx ::subs/selection-info)
   (get-token-from ::selection)])

(def token-from-interceptors
  "Get all tokens(id) passed to interceptor"
  [rf/trim-v
   (rf/inject-cofx ::subs/selection-info)
   (get-token-from ::interceptor)])

(def line-from-interceptors
  "Get all lines(id) passed to interceptor"
  [rf/trim-v
   (rf/inject-cofx ::subs/selection-info)
   (get-line-from ::interceptor)])

(rf/reg-event-db
  ::token-update
  (fn [db [_ info token-updated]]
    (let [{:keys [line token]} info]
      (assoc-in db [:lines line :tokens token] token-updated))))

(rf/reg-event-db
  ::token-delete
  (fn [db [_ info]]
    (let [line (get-in db [:lines (:line info) :tokens])]
      (assoc-in
        db
        [:lines (:line info) :tokens]
        (normalize-tokens (dissoc line (:token info)))))))

(defn- insert-char [string char idx]
  (str (subs string 0 idx) char (subs string idx)))

(defn token-insert [{:keys [content] :as token} key offset]
  (-> token
      (assoc :content (insert-char content key offset))))

(defn remove-char
  [content offset]
  (let [length (count content)]
    (str (subs content 0 (dec offset))
         (subs content offset length))))

(defn token-remove [{:keys [content] :as token} offset]
  (-> token
      (assoc :content (remove-char content offset))))

(defn token-concat [x y]
  (-> x
      (assoc :content (str (:content x) (:content y)))))

(rf/reg-event-fx
  ::token-concat
  token-from-interceptors
  (fn [{:keys [tokens selection]} [x y]]
    (let [line  (:line selection)
          token (:token selection)]
      {:fx [[:dispatch [::token-update
                        {:line line :token (dec token)}
                        (reduce token-concat tokens)]]
            [:dispatch [::token-delete selection]]]})))

(defn normalize-tokens-vec [form]
  (reduce
    into 
    (map-indexed
      (fn [idx [_ token]]
        {idx token})
      form)))

(rf/reg-event-db
  ::line-update
  (fn [db [_ id line]]
    (assoc-in db [:lines id] line)))

(rf/reg-event-db
  ::line-delete
  (fn [db [_ id]]
    (let [lines (get-in db [:lines])]
      (assoc-in db [:lines] (normalize-lines (dissoc lines id))))))

(defn count-tokens [line]
  (count (seq (:tokens line))))

(rf/reg-event-fx
  ::line-wrap-back
  line-from-interceptors
  (fn [{:keys [lines selection]} [curr-line prev-line]]
    (let [curr        (first lines)
          prev        (first (next lines))
          prev-length (count-tokens prev)
          normalized  (normalize-tokens-vec
                        (concat []
                                (:tokens prev)
                                (:tokens curr)))
          updated     (update curr :tokens merge normalized)
          cursor      (-> default-cursor
                          (assoc :line prev-line)
                          (assoc :token prev-length))]
      {:dispatch-n [[::line-update prev-line updated]
                    [::line-delete curr-line]
                    [::cursor-update cursor]]})))

(rf/reg-event-fx
  ::token-delete-char
  (conj
    token-interceptors
    (rf/inject-cofx ::subs/has-next-token?))
  (fn [{:keys [token selection has-next-token?]} _]
    (let [token-id (:token selection)
          line-id  (:line selection)
          offset   (:offset selection)]
      
      (cond
        (spec/wrap-back? offset line-id token-id)
        {:dispatch [::line-wrap-back line-id (dec line-id)]}

        (spec/token-start? offset)
        (if (spec/can-concat? line-id token-id)
          {:dispatch [::token-concat (dec token-id) token-id]}
          (prn "first line do nothing"))
        
        (spec/token-body? offset)
        (let [token (token-remove token offset)]
          (if (empty? (:content token))
            
            (if (not= token-id 0)
              {:dispatch-n [[::token-delete selection]
                            [::cursor-update
                             (-> selection
                                 (assoc :token (dec token-id)))]]}
              
              (if has-next-token?
                {:dispatch-n [[::token-delete selection] ; delete token
                              [::cursor-update
                               (-> selection
                                   (assoc :token (dec token-id)))]]}
                
                (if (or (not= line-id 0)
                        (not= token-id 0))
                  ;; `TODO`: make better cursor selection
                  {:dispatch-n [[::line-delete line-id]
                                [::cursor-update
                                 (-> selection
                                     (assoc :line (dec line-id)))]]}
                  
                  (prn "Last char on editor. Do something for last char"))))
            
            {:dispatch-n [[::token-update selection token]
                          [::cursor-update
                           (-> selection
                               (assoc :offset (dec offset)))]]})
          
          )))))

(rf/reg-event-fx
  ::token-insert-char
  token-interceptors
  (fn [{:keys [token selection] :as coeffect} [_ key]]
    (let [{:keys [offset]} selection]
      {:fx [[:dispatch [::token-update selection
                        (token-insert token key offset)]]
            [:dispatch [::cursor-update
                        (-> selection
                            (assoc :offset (inc offset)))]]]})))

(rf/reg-event-db
  ::init-state
  (fn
    [_ _]
    initial-state))
