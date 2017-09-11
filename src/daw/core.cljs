(ns daw.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [daw.events]
            [daw.subs]
            [daw.views :as views]))


(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
    (.getElementById js/document "app")))

(defn init! []
  (re-frame/dispatch-sync [:initialize-db])
  (mount-root))
