(ns ^:figwheel-always store.mixin
  (:require [rum.core :as rum]
            [store.core :as store]))

(defn store!
  ([state action]
   (store! state action nil))
  ([state action effect]
   {:pre [(or (map? state) (ifn? state))
          (ifn? action)
          (or (nil? effect) (ifn? effect))]}
   {:init
    (cond
      (map? state)
      (fn [rum-state _]
        (assoc rum-state :rum/local (atom state)))
      (ifn? state)
      (fn [{:keys [rum/args] :as rum-state} _]
        (assoc rum-state :rum/local
               (atom (apply state args))))
      :else
      (throw (ex-info "State must be a map or func." {})))
    :will-mount
    (fn [{:keys [rum/local rum/react-component] :as rum-state}]
      (add-watch local :rum/local
                 (fn [_ _ old-state new-state]
                   (when-not (= old-state new-state)
                     (rum/request-render react-component))))
      (assoc rum-state
             :rum/state @local
             :rum/action
             (fn action! [action' data]
               (let [state' (swap! local action
                                   [action' data])]
                 (when effect
                   (effect state' [action' data
                                   {:rum/action action!}]))))))
    :before-render
    (fn [{:keys [rum/local] :as rum-state}]
      (swap! store/state
             (fn [global-state local-state]
               (merge global-state local-state))
             @local)
      (assoc rum-state :rum/state @local))}))
