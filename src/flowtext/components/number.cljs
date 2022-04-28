(ns ^:fighweel-always flowtext.components.number
  (:require [rum.core :as r]))

(r/defc number < r/static [n]
  [:h4 (inc n)])
