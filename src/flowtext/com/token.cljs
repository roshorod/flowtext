(ns ^:fighweel-always flowtext.com.token)

(defn Token [index {:keys [content]}]
  ^{:key index}
  [:span.token-content
   {:token index}
   content])
