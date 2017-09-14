(ns daw.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [mozart.audio :as audio]
            [daw.events]
            [daw.subs]
            [daw.views :as views]
            [goog.events :as gevents]
            [goog.events.EventType :refer [KEYDOWN KEYPRESS KEYUP]]))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
    (.getElementById js/document "app")))

(def key->note
  {"a" 60
   "w" 61
   "s" 62
   "e" 63
   "d" 64
   "f" 65
   "t" 66
   "g" 67
   "y" 68
   "h" 69
   "u" 70
   "j" 71
   "k" 72
   "o" 73
   "l" 74
   "p" 75})

(defn add-handler [event handler]
  (.addEventListener js/document event handler))

(defn dispatch-keynote [type evt]
  (if-let [note (-> evt .-key key->note)]
    (re-frame/dispatch [type note])))

(defn init! []
  (re-frame/dispatch-sync [:initialize-db])
  (gevents/listen js/document KEYDOWN (partial dispatch-keynote :note-on))
  (gevents/listen js/document KEYUP (partial dispatch-keynote :note-off))
  (mount-root))
