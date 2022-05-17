(ns ^:figwheel-hooks flowtext.common.normalized)

(defn normalize-sort [form]
  (into (sorted-map) form))

(defn normalize-index-map [form]
  (reduce
    into
    (map-indexed
      (fn [idx val]
        {idx (reduce into (next val))})
      form)))

(defn normalize-tokens [form]
  (-> form
      normalize-sort
      normalize-index-map))

(defn normalize-lines [form]
  (-> (map
        (fn [line]
          (let [tokens     (reduce into (next line))
                normalized (normalize-tokens
                             (get tokens :tokens))]
            {(first line) (assoc tokens :tokens normalized)}))
        form)
      normalize-sort
      normalize-index-map))

(defn normalize-tokens-vec [form]
  (reduce
    into 
    (map-indexed
      (fn [idx [_ token]]
        {idx token})
      form)))
