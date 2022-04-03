(ns ^:fighweel-always components.editor
  (:require [rum.core :as r]
            [cljs.core.async :refer [>! timeout]]
            [cljs.core.async :refer-macros [go]]
            [core.state :refer [dispatch subscribe]]
            [core.callback])
  (:refer-clojure :exclude [range]))

(comment
  (defn append-index [map]
    (->> map
         (keep-indexed (fn [index val]
                         (assoc val :id index)))))
  (defn insert-by-index [map index elem]
    (let [[before after] (split-at index map)]
      (append-index
        (into [] (concat before [elem] after)))))
  
  )

(defn token-select [selection node offset]
  (let [range (.createRange js/document)
        text  (.-firstChild node)]
    (js/console.log node offset)
    (.collapse range false)
    (.selectNode range text)
    (.setStart range text offset)
    (.setEnd range text offset)
    (.removeAllRanges selection)
    (.addRange selection range)))

(defn insert-char [string char index]
  (str (subs string 0 index) char (subs string index)))

(defn insert-word [event]
  (let [selection (js/getSelection)
        offset    (.-focusOffset selection)
        range     (.createRange js/document)
        key       (.-key event)
        node      (.-focusNode selection)
        rnode     (js/ReactDOM.findDOMNode
                    (.-parentElement node))
        line-id   (.getAttribute (.-parentElement rnode) "line")
        token-id  (.getAttribute rnode "token")
        content   (.-innerText rnode)]
    (go
      (dispatch
        :token-update
        {:line-id  (js/parseInt line-id)
         :token-id (js/parseInt token-id)
         :content  (insert-char content key offset)})
      (.removeAllRanges selection)
      ;; Some time selection lost node when key input so fast.
      ;; And that's make old node selection and
      ;; move cursor to the start of node.
      (<! (timeout 40))
      (token-select selection rnode (inc offset)))))

(def handle-input
  (fn [event]
    (let [_ (.preventDefault event)]
      (insert-word event))))

(r/defc token < r/static [{:keys [content id]}]
  [:span {:token id} content])

(r/defc line < r/static [{:keys [tokens id]}]
  [:p{:line id}
   (->> tokens
        (mapv
          (fn [token-props]
            (-> (token token-props)
                (r/with-key (:id token-props))))))])

(def initial-lines
  [{:id 0 :tokens [{:id 0 :content "first "}
                   {:id 1 :content "second "}
                   {:id 2 :content "third "}]}
   {:id 1 :tokens [{:id 0 :content "first "}
                   {:id 1 :content "second "}
                   {:id 2 :content "third "}]}
   {:id 2 :tokens [{:id 0 :content "first "}
                   {:id 1 :content "second "}
                   {:id 2 :content "third "}]}
   {:id 3 :tokens [{:id 0 :content "first "}
                   {:id 1 :content "second "}
                   {:id 2 :content "third "}]}])

(def initial-state
  {:lines initial-lines})

(dispatch :initial-state initial-state)

(r/defc editor < r/reactive []
  (let [lines (subscribe :lines)]
    [:div#editor
     {:content-editable                  true
      :suppress-content-editable-warning true
      :on-key-down                       handle-input}
     (->> lines
          (mapv
            (fn [line-props]
              (-> (line line-props)
                  (r/with-key (:id line-props))))))]))
