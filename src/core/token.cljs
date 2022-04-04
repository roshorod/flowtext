(ns ^:figwheel-always core.token
  (:refer-clojure :exclude [char])
  (:require [cljs.core.async :refer-macros [go]]
            [cljs.core.async :refer [<! >! timeout]]
            [core.state :refer [dispatch]]))

(defn- insert [string char index]
  (str (subs string 0 index) char (subs string index)))

(defmulti token identity)

(defmethod token :selection/node [_]
  (let [selection (js/getSelection)
        node      (.-focusNode selection)]
    (try
      (js/ReactDOM.findDOMNode
        (.-parentElement node)) ;; Selection select text of node
      (catch :default _
        (throw
          (ex-info "Canno't find react node." {}
                   :react-node-error))))))

(defmethod token :insert/char [_ char]
  (go
    (let [selection (js/getSelection)
          node      (token :selection/node)
          offset    (-> selection .-focusOffset)
          line-id   (-> node
                        .-parentElement
                        (.getAttribute "line"))
          token-id  (.getAttribute node "token")
          content   (.-innerText node)]
      (dispatch
        :token-update
        {:line-id  (js/parseInt line-id)
         :token-id (js/parseInt token-id)
         :content  (insert content char offset)})
      (.removeAllRanges selection)
      (<! (timeout 50))
      (core.input/input
        :select/token selection node (inc offset)))))
