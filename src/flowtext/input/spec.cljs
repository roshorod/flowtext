(ns ^:figwheel-always flowtext.input.spec
  (:require [cljs.spec.alpha :as s]))

(def text-key-regex #"^[a-zA-Z0-9|._%+$&+,:;=?@#]")
(def left-key-regex #"^ArrowLeft")
(def right-key-regex #"^ArrowRight")
(def space-key-regex #"^ ")
(def backspace-key-regex #"^Backspace")
(def enter-key-regex #"^Enter")
(def down-key-regex #"^ArrowDown")
(def up-key-regex #"^ArrowUp")

(s/def ::text-key (s/and string? #(re-matches text-key-regex %)))
(s/def ::left-key (s/and string? #(re-matches left-key-regex %)))
(s/def ::right-key (s/and string?
                          #(re-matches right-key-regex %)))
(s/def ::space-key
  (s/and string? #(re-matches space-key-regex %)))
(s/def ::backspace-key
  (s/and string? #(re-matches backspace-key-regex  %)))
(s/def ::enter-key
  (s/and string? #(re-matches enter-key-regex %)))
(s/def ::down-key
  (s/and string? #(re-matches down-key-regex %)))
(s/def ::up-key
  (s/and string? #(re-matches up-key-regex %)))

(def text? #(s/valid? ::text-key %))
(def left? #(s/valid? ::left-key %))
(def right? #(s/valid? ::right-key %))
(def space? #(s/valid? ::space-key %))
(def backspace? #(s/valid? ::backspace-key %))
(def enter? #(s/valid? ::enter-key %))
(def down? #(s/valid? ::down-key %))
(def up? #(s/valid? ::up-key %))

(s/def ::nil-offset (s/and number? zero?))
(s/def ::offset (s/and number? pos-int?))

(def wrap-offset?
  (fn [offset line token]
    (and (s/valid? ::nil-offset offset)
         (s/valid? ::offset line)
         (s/valid? ::nil-offset token))))
(def start-offset? #(s/valid? ::nil-offset %))
(def offset? #(s/valid? ::offset %))
