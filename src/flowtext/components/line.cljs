(ns ^:fighweel-always flowtext.components.line
  (:require [rum.core :as r]
            [flowtext.components.token :refer [token]]))

(r/defc line < r/static [{:keys [tokens id]}]
  [:p.line
   {:line id}
   (->> tokens
        (mapv #(-> (token %)
                   (r/with-key (:id %)))))])
