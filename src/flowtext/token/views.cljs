(ns ^:fighweel-always flowtext.token.views)

(defn Token [index {:keys [content] }]
  ^{:key index}
  [:span.token-content {:token index} content])
