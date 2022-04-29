(ns ^:fighweel-always flowtext.controllers.select
  (:refer-clojure :exclude [next])
  (:require [flowtext.input.select :as s]
            [flowtext.data.token :as t]
            [flowtext.data.line :as l]
            [flowtext.data.node :as n]))

(defmulti control (fn [event] event))

(defmethod control :init [_ _ state]
  {:state (js/getSelection)})

(defmethod control :next/offset [_ _ state]
  (let [node    (n/get-node state)
        content (:text node)
        offset  (inc (:offset node))]
    (if (<= offset (.-length content))
      (s/select offset content)
      {:dispatch {:control :select :action :next/token}})))

(defmethod control :prev/offset [_ _ state]
  (let [node    (n/get-node state)
        content (:text node)
        offset  (dec (:offset node))]
    (if (>= offset 0)
      (s/select offset content)
      {:dispatch {:control :select :action :prev/token}})))

(defmethod control :next/token [_ _ state]
  (let [node (n/get-node state)
        next @(:next node)]
    (if (nil? next)
      {:dispatch {:control :select
                  :action  :next/line
                  :args    {:offset 0 :token-id 0}}}
      (s/select 1 (n/node->text next)))))

(defmethod control :prev/token [_ _ state]
  (let [node (n/get-node state)
        prev @(:prev node)]
    (if (nil? prev)
      {:dispatch {:control :select
                  :action  :prev/line
                  :args    {:offset
                            (l/node->prev-line-last-token-offset (:node node))
                            :token-id
                            (t/find-last-token-id-of-prev-line (:node node))}}}
      (s/select (dec (n/node->text-length prev))
                (n/node->text prev)))))

(defmethod control :next/line [_ args state]
  (let [[{:keys [offset token-id]}] args]
    (let [node     (n/get-node state)
          token-id (if (nil? token-id)
                     (t/node->token-id (:node node))
                     token-id)
          line     (l/node->next-line (:node node))
          offset   (if (nil? offset)
                     0
                     offset)]
      (try
        (s/select offset
                  (n/node->text
                    (t/find-token-by-id line token-id)))
        (catch js/TypeError _
          (s/select 0
                    (n/node->text
                      (t/find-token-by-id line 0))))))))

(defmethod control :prev/line [_ args state]
  (prn args)
  (let [[{:keys [offset token-id]}] args]
    (let [node     (n/get-node state)
          token-id (if (nil? token-id)
                     (t/node->token-id (:node node))
                     token-id)
          line     (n/node->prev-line (:node node))
          offset   (if (nil? offset)
                     0
                     offset)]
      (try 
        (s/select offset
                  (n/node->text
                    (t/find-token-by-id line token-id)))
        (catch js/TypeError _
          (s/select 0
                    (n/node->text
                      (t/find-token-by-id line 0))))))))
