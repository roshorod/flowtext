(ns ^:fighweel-always flowtext.com.layout
  (:require [re-frame.core :as rf]
            [flowtext.subs :as subs]
            [flowtext.com.editor :refer [Editor]]))

(defn Layout []
  (let [lines (rf/subscribe [::subs/lines])]
    [Editor lines]))
