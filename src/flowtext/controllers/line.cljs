(ns ^:fighweel-always flowtext.controllers.line
  (:require [flowtext.utils :as utils]))

(def initial-state
  [{:id 0 :tokens [{:id 0 :content "first "}
                   {:id 1 :content "second "}
                   {:id 2 :content "third"}
                   {:id 3 :content " "}]}
   {:id 1 :tokens [{:id 0 :content "first "}
                   {:id 1 :content "second "}
                   {:id 2 :content "third"}
                   {:id 3 :content " "}]}
   {:id 2 :tokens [{:id 0 :content "first "}
                   {:id 1 :content "second "}
                   {:id 2 :content "third"}
                   {:id 3 :content " "}]}
   {:id 3 :tokens [{:id 0 :content "first "}
                   {:id 1 :content "second "}
                   {:id 2 :content "third"}
                   {:id 3 :content " "}]}])

(defmulti control (fn [event] event))

(defmethod control :default [_ _ state]
  {:state state})

(defmethod control :init []
  {:state initial-state})

(defmethod control :token/update [_ args state]
  (let [[{:keys [line-id token-id content offset node]}] args]
    {:state
     (assoc-in state [line-id :tokens token-id]
               (-> (get-in state [line-id :tokens token-id])
                   (assoc :content content)))
     :input {:action :select :node node :offset offset}}))

(defmethod control :line/append [_ args state]
  (let [[{:keys [line-id token-id map offset node]}] args]
    (let [tokens (->> (get-in state [line-id :tokens])
                      (filterv #(not= token-id (:id %))))]
      {:state
       (assoc-in state [line-id :tokens]
                 (utils/assoc-token-map tokens map token-id))
       :input {:action :select :node node :offset offset}})))
