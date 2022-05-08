(ns ^:figwheel-always flowtext.input.backspace
  ;; (:require [flowtext.input.token :as token]
  ;;           [flowtext.input.selection :as selection]
  ;;           [flowtext.input.utils :as utils]
  ;;           [flowtext.input.spec :as spec]
  ;;           [citrus.core :as citrus])
  )

;; (defn backspace [r]
;;   (let [{:keys
;;          [offset line token content prev node]
;;          :as payload}
;;         @token/token!]
;;     (cond
;;       (spec/wrap-offset? offset line token)
;;       (citrus/dispatch! r :lines :line/wrap-back payload)
      
;;       (spec/start-offset? offset)
;;       (let [payload
;;             (-> payload
;;                 (assoc :node @prev)
;;                 (assoc :content
;;                        (utils/content->remove content offset))
;;                 (assoc :offset
;;                        (utils/valid-offset (dec offset))))]
;;         (citrus/dispatch! r :lines :token/concat payload))
      
;;       (spec/offset? offset)
;;       (let [payload
;;             (-> payload
;;                 (assoc :content
;;                        (utils/content->remove content offset))
;;                 (assoc :offset
;;                        (utils/valid-offset (dec offset))))]
;;         (if (empty? (:content payload))
;;           (citrus/dispatch! r :lines :token/delete payload)
;;           (citrus/dispatch! r :lines :token/update payload))))))
