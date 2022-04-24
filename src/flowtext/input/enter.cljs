(ns ^:fighweel-always flowtext.input.enter
  (:require [flowtext.input.token :as token]
            [citrus.core :as citrus]))

(defn enter [r]
  (let [payload @token/token!]
    (citrus/dispatch! r :lines :line/wrap-next payload)))
