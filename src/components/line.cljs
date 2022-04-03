(ns ^:fighweel-always components.line
  (:require [rum.core :as r]
            [components.token :refer [token]]))

(r/defc line < r/static [{:keys [tokens id]}]
  [:p{:line id}
   (->> tokens
        (mapv #(-> (token %)
                   (r/with-key (:id %)))))])
