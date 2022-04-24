(ns ^:figwheel-always flowtext.input.insert
  (:require [flowtext.input.token :as token]
            [flowtext.input.utils :as utils]
            [citrus.core :as citrus]))

(defn insert [r char]
  (let [{:keys [content offset] :as payload} @token/token!]
    (let [payload
          (-> payload
              (assoc
                :content
                (utils/char->content content char offset))
              (assoc :offset (inc offset)))]
      (citrus/dispatch! r :lines :token/update payload))))
