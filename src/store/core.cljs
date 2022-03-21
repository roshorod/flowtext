(ns ^:figwheel-always store.core)

(defonce state (atom {}))

(defmulti action! (fn [state [action data]] action))

(defmulti effect! (fn [[old-state new-state] [action data]]
                    action))

(defn dispatch! [action data]
  (let [old-state @state
        new-state (swap! state action! [action data])]
    (when effect!
      (effect! [old-state new-state]
               [action data {:flowtext/action dispatch!}]))))

(defn enable-state-print! []
  (add-watch
    state
    {}
    (fn [k r old new]
      (println new))))
