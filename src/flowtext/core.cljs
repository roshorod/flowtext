(ns ^:figwheel-hooks flowtext.core
  (:require [rum.core :as rum]
            [store.core :as store]
            [store.mixin :as mixin]))

(enable-console-print!)
(store/enable-state-print!)

(rum/defcs section <
  (mixin/store! (fn [{:keys [default-open?]}]
                  (if (some? default-open?)
                    {:open? default-open?}
                    {:open? false}))
                (fn [state [action _]]
                  (case action
                    :toggle   (update state :open? not)
                    :increase (update state :counter inc))))
  [{:keys [rum/action rum/state]}
   {:keys [title size default-open? content]}]
  [:div.content
   [:button.content__button
    {:on-click
     (fn [_] (action :toggle))}
    "Show counter"]
   (if (:open? state)
     [:div.hidden-content
      [:h2.hidden-content__title title]
      [:h4.hidden-content__text content]
      [:div.hidden-content__counter.counter
       [:h4.counter__number (or (:counter state) 0)]
       [:button.counter__button_increase
        {:on-click
         (fn [_]
           (action :increase))}
        "Increase counter"]]])])

(defn ^:after-load re-render []
  (rum/mount
    (section
      {:title         "Counter section"
       :content       "Hello from counter state"
       :default-open? true})
    (js/document.getElementById "root")))

(defonce start-up (re-render))
