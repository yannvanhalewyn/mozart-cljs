(ns daw.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [daw.audio]
            [daw.events]
            [daw.subs]
            [daw.views :as views]))


(defn mount-root []
  (.log js/console "Mount root called")
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
    (.getElementById js/document "app")))

(defn init! []
  (.log js/console "Init called")
  (re-frame/dispatch-sync [:initialize-db])
  (mount-root))
