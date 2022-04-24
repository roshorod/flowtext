(ns ^:figwheel-always flowtext.input.whitespace
  (:require [flowtext.input.token :as token]
            [flowtext.input.utils :as utils]
            [citrus.core :as citrus]))

(defn whitespace [r]
  (let [{:keys
         [offset content node next]
         :as payload}
        @token/token!]
    (let [left          (utils/node->token-id node)
          right         (inc left)
          left-content  (str (subs content 0 offset) " ")
          right-content (subs content offset (count content))
          payload
          (-> payload
              (assoc :offset 0)
              (assoc :node @next)
              (assoc
                :map
                (-> []
                    (conj {:id left :content left-content})
                    (conj {:id right :content right-content}))))]
      (citrus/dispatch! r :lines :token/append payload))))
