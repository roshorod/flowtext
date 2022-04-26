(ns ^:fighweel-always flowtext.data.wrapper
  (:require [flowtext.data.token :as t]
            [flowtext.data.line :as l]))

(defn map-token [line]
  (mapv #(t/token (:id %) (:content %)) (get-in line [:tokens])))

(defn map-line [line]
  (l/line (:id line) (map-token line)))

(defprotocol Data-Mapper
  (data-map [this]))

(defrecord Lines-Wrapper [data]
  Data-Mapper
  (data-map [this]
    (assoc this :data (mapv map-line (:data this)))))

(defn wrapper [map]
  (Lines-Wrapper. map))
