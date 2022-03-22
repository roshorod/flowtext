(ns ^:figwheel-hooks flowtext.core
  (:require [cljs.spec.alpha :as s]
            [clojure.string :refer [split replace]]
            [rum.core :as rum]
            [store.core :as store]
            [store.mixin :as mixin]))

(enable-console-print!)
(store/enable-state-print!)

(def line-class "editor__line")
(def token-class "line__token")

(defn make-element [^String content]
  (let [element (.createElement js/document "span")
        content (replace content #"&nbsp;" " ")]
    (if (= content "<br>")
      (do (set! (.-textContent element) "")
          (.appendChild element
                        (.createElement js/document "br")))
      (set! (.-textContent element) content))
    (set! (.-className element) token-class)
    element))

(defn make-empty-line [target]
  (let [element (.createElement js/document "div")
        span    (.createElement js/document "span")]
    (set! (.-className element) line-class)
    (set! (.-className span) token-class)
    (set! (.-innerHTML span) "<br>")
    (.appendChild element span)
    (.appendChild target element)
    element))

(defn token->line [token]
  (loop [line? (.-parentElement token)]
    (if (= (.-className line?) line-class)
      line?
      (recur (.-parentElement line?)))))

(def line-string "<div class=\"editor__line\"><span class=\"line__token\">one</span><span class=\"line__token\">two</span></div>")

(def token-regex #"<span class=\"line__token\">.*?</span>")
(def content-regex #"(?<=\>).*(?=\<)")

(def get-line-tokens #(re-seq token-regex %))
(def get-token-content #(re-seq content-regex %))

(defn token-map-to-line [line map]
  (doseq [token map]
    (.appendChild line (make-element token))))

(defn transform-line [line]
  (let [tokens (get-line-tokens (.-outerHTML line))
        _      (.replaceChildren line)]
    (doseq [token tokens]
      (let [content (get-token-content token)
            token-map (split (clj->js content) #" ")]
        (token-map-to-line line token-map)))))

(def timer (atom nil))

(defn load-editor [editor]
  (.addEventListener
    editor
    "keyup"
    (fn [event]
      (let [current-line (.getSelection js/window)
            current-node (.-focusNode current-line)]
        (js/clearTimeout @timer)
        (swap!
          timer
          #(js/setTimeout
             (fn []
               (if-let
                   [not-editor-node
                    (not
                      (and (= editor current-node)
                           ;; if after empty line exist other line,
                           ;; that prevent adding empty line,
                           ;; when delete first line
                           (not
                             (= (.-innerHTML current-node) "<br>"))))]
                 (transform-line (token->line current-node))
                 (make-empty-line editor)))
             5000)))))
  (set! (.-contentEditable editor) true))

(rum/defc editor-layout []
  [:div#editor
   [:div.editor__line
    [:span.line__token "One two"]
    [:span.line__token "three"]]])

(defn post-render []
  (let [editor (.getElementById js/document "editor")]
    (load-editor editor)))

(defn ^:after-load re-render []
  (rum/mount
    (editor-layout)
    (.getElementById js/document "root"))
  (post-render))

(defonce start-up (re-render))
