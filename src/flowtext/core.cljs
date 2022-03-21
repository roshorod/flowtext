(ns ^:figwheel-hooks flowtext.core
  (:require [cljs.spec.alpha :as s]
            [clojure.string :refer [split]]
            [rum.core :as rum]
            [store.core :as store]
            [store.mixin :as mixin]))

(enable-console-print!)
(store/enable-state-print!)

(def line-class "editor__line")
(def token-class "line__token")

(defn make-element [^String content]
  (let [element (.createElement js/document "span")]
    (set! (.-textContent element) content)
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

(defn transform-line [line]
  (doseq [token (get-line-tokens (.-outerHTML line))]
    (let [content (get-token-content token)]
      (prn content))))

(defn load-editor [editor]
  (.addEventListener
    editor
    "keyup"
    (fn [event]
      (let [current-line (.getSelection  js/window)
            current-node (.-focusNode current-line)]
        ;; If false:
        ;;; Make impossible replacing contenteditable node
        ;; If true:
        ;;; Make text tokens.
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
          ;; (js/console.log "Tokenize")
          ;; (js/console.log (clj->js current-line))
          ;; (js/console.log (token->line current-node))
          ;; (js/console.log (.-parentElement current-node))
          (make-empty-line editor))
        
        
        ;; Take until enter was pressed or time out of 2-5 sec.
        ;; https://stackoverflow.com/questions/4220126/run-javascript-function-when-user-finishes-typing-instead-of-on-key-up
        
        ;; Then take outerHTML form line parse it and add child elements
        
        ;; May make content checking of old string and new and old tokens,
        ;; That may make elements without re-rendering.
   
        )))
  (set! (.-contentEditable editor) true))

(rum/defc editor-layout []
  [:div#editor
   [:div.editor__line
    [:span.line__token "One two "]
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
