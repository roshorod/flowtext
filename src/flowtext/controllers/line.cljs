(ns ^:fighweel-always flowtext.controllers.line
  (:refer-clojure :exclude [map])
  (:require [flowtext.input.utils :as utils]
            [flowtext.input.token :as token]
            [citrus.core :as citrus]
            [clojure.string :refer [trim]]))

(def initial-state
  [{:id 0 :tokens [{:id 0 :content "first "}
                   {:id 1 :content "second "}
                   {:id 2 :content "third"}]}
   {:id 1 :tokens [{:id 0 :content "first "}
                   {:id 1 :content "second "}
                   {:id 2 :content "third"}]}
   {:id 2 :tokens [{:id 0 :content "first "}
                   {:id 1 :content "second "}
                   {:id 2 :content "third"}]}
   {:id 3 :tokens [{:id 0 :content "first "}
                   {:id 1 :content "second "}
                   {:id 2 :content "third"}]}])

(defmulti control (fn [event] event))

(defmethod control :default [_ _ state]
  {:state state})

(defmethod control :init []
  {:state initial-state})

(defmethod control :token/update [_ args state]
  (let [[{:keys [line token content offset node]}] args]
    {:state
     (assoc-in
       state
       [line :tokens token]
       (-> (get-in state [line :tokens token])
           (assoc :content content)))
     :input {:action :select :node node :offset offset}}))

(defmethod control :token/append [_ args state]
  (let [[{:keys [line token map offset node select]}] args]
    (let [tokens (->> (get-in state [line :tokens])
                      (filterv #(not= token (:id %))))]
      (-> {:state
           (assoc-in
             state
             [line :tokens]
             (utils/assoc-token-map tokens map token))}
          (cond-> (or (nil? select) (true? select))
            (assoc
              :input
              {:action :select :node node :offset offset}))))))

(defmethod control :token/concat [_ args state]
  (let [[{:keys [line token content offset node]}] args]
    (let [left   (get-in state [line :tokens (dec token)])
          tokens (->> (get-in state [line :tokens])
                      (filterv #(not= token (:id %)))
                      (filterv #(not= (:id left) (:id %))))
          new    (-> left
                     (assoc :content
                            (utils/content->remove
                              (str (:content left) content)
                              (count (:content left)))))]
      {:state
       (assoc-in
         state
         [line :tokens]
         (utils/assoc-token-map tokens [new] (:id left)))
       :input {:action :select
               :node   node
               :offset (dec (count (:content left)))}})))

(defmethod control :token/delete [_ args state]
  (let [[{:keys [line token node select prev]}] args]
    (let [tokens (->> (get-in state [line :tokens])
                      (filterv #(not= token (:id %))))
          prev   @prev]
      (-> {:state (assoc-in state [line :tokens] tokens)}
          (cond-> (and (or (nil? select)
                           (true? select))
                       (not= (nil? prev)))
            (assoc
              :input
              {:action :select
               :node   prev
               :offset (utils/node->length prev)}))
          (cond-> (nil? prev) ;; Select previous line
            (assoc
              :input
              {:action :select
               :node   (utils/line->last-token
                         (utils/node->prev-line node))}))))))

(defmethod control :line/wrap-back [_ args state]
  (let [[{:keys [line node select]}] args]
    (let [curr  (get-in state [line :tokens])
          prev  (get-in state [(dec line) :tokens])
          new   (utils/assoc-token-map prev curr (count prev))
          state (utils/assoc-tokens-index
                  (filterv some?
                           (map-indexed
                             #(if (not= line %1) %2) state)))
          node  (utils/line->last-token
                  (utils/node->prev-line node))]
      (-> {:state (assoc-in state [(dec line) :tokens] new)}
          (cond-> (or (nil? select) (true? select))
            (assoc
              :input
              {:action :select
               :node   node
               :offset (utils/node->length node)}))))))

(defmethod control :line/wrap-next [_ args state]
  (let [[{:keys [line token content node offset select]}] args]
    (let [left      (subs content 0 offset)
          right     (subs content offset (count content))
          tokens    (get-in state [line :tokens])
          state     (filter #(not= line (:id %)) state)
          curr-line (into (->> tokens
                               (filterv #(> token (:id %))))
                          [{:id token :content
                            (if (empty? left)
                              " "
                              left)}])
          
          next-line (->> tokens
                         (filter #(and (not= token (:id %))
                                       (< token (:id %))))
                         (into [{:id 0 :content
                                 (if (empty? right)
                                   " "
                                   right)}])
                         (utils/assoc-tokens-index))
          
          map (if (js/isNaN line)
                []
                [{:id line :tokens curr-line}
                 {:id (inc line) :tokens next-line}])]
      (-> {:state (utils/assoc-token-map state map line)}
          (cond-> (or (nil? select) (true? select))
            (assoc
              :input
              {:action :select
               :node   (utils/line->first-token
                         (utils/node->next-line node))
               :offset 0}))))))
