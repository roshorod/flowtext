(ns ^:figwheel-always flowtext.spec
  (:require [cljs.spec.alpha :as s]))

(def text-key-regex #"^[a-zA-Z0-9|._%+$&+,:;=?@#]")
(def left-key-regex #"^ArrowLeft")
(def right-key-regex #"^ArrowRight")
(def space-key-regex #"^ ")
(def backspace-key-regex #"^Backspace")

(s/def ::text-key (s/and string? #(re-matches text-key-regex %)))
(s/def ::left-key (s/and string? #(re-matches left-key-regex %)))
(s/def ::right-key (s/and string?
                          #(re-matches right-key-regex %)))
(s/def ::space-key
  (s/and string? #(re-matches space-key-regex %)))
(s/def ::backspace-key
  (s/and string? #(re-matches backspace-key-regex  %)))

(def text? #(s/valid? ::text-key %))
(def left? #(s/valid? ::left-key %))
(def right? #(s/valid? ::right-key %))
(def space? #(s/valid? ::space-key %))
(def backspace? #(s/valid? ::backspace-key %))
