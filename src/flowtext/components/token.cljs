(ns ^:fighweel-always flowtext.components.token
  (:require [rum.core :as r]))

(r/defc token < r/static [{:keys [content id]}]
  [:span {:token id} content])
