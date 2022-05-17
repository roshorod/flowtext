(ns ^:fighweel-always flowtext.cursor.core)

(defn attribute-selector [name value]
  (str "[" name "=\"" value "\"]"))

(def line-property "line")
(def token-property "token")

(defn token-selector
  "Return node with token attribute.

  If not found return nil.
  
  `js/querySelectorAll` can be used with all
  css selectors like > even with pseudo-classes

  "
  [line token]
  (js/document.querySelector
    (str (attribute-selector line-property line)
         '>
         (attribute-selector token-property token))))

(defn get-token-id [selection]
  (-> selection
      .-focusNode
      .-parentElement
      (.getAttribute "token")
      js/parseInt))

(defn get-line-id [selection]
  (-> selection
      .-focusNode
      .-parentElement
      .-parentElement
      (.getAttribute "line")
      js/parseInt))

(defn get-offset [selection]
  (-> selection
      .-focusOffset))

(defn get-token-info [selection]
  {:line   (get-line-id selection)
   :token  (get-token-id selection)
   :offset (get-offset selection)})
