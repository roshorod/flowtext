(ns ^:fighweel-always flowtext.data.line)

(defrecord Line [id tokens])

(defn line [id tokens]
  (map->Line {:id id :tokens tokens}))
