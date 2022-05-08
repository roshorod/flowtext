(ns ^:fighweel-always flowtext.com.line
  (:require [flowtext.com.token :as t]))

(defn Line [index {:keys [tokens]}]
  [:div.line-wrapper
   [:span.line-number
    {:content-editable false}
    (inc index)]
   [:div.line-tokens.tokens-wrapper
    {:line index}
    (map-indexed
      (fn [idx token]
        ^{:key idx}
        [t/Token idx (reduce into (rest token))])
      tokens)]])
