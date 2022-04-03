(ns ^:figwheel-always core.token
  (:refer-clojure :exclude [char])
  (:require [cljs.core.async :refer-macros [go]]
            [cljs.core.async :refer [<! >! timeout]]
            [core.state :refer [dispatch]]))

(defn- insert [string char index]
  (str (subs string 0 index) char (subs string index)))

(defmulti token identity)

(defmethod token :insert/char [_ char]
  (go
    (let [selection (js/getSelection)
          node      (.-focusNode selection)
          rnode     (js/ReactDOM.findDOMNode
                      (.-parentElement node))
          offset    (-> selection .-focusOffset)
          line-id   (.getAttribute
                      (.-parentElement rnode) "line")
          token-id  (.getAttribute rnode "token")
          content   (.-innerText rnode)]
      (dispatch
        :token-update
        {:line-id  (js/parseInt line-id)
         :token-id (js/parseInt token-id)
         :content  (insert content char offset)})
      (.removeAllRanges selection)
      (<! (timeout 50))
      (core.input/input
        :select/token selection rnode (inc offset)))))