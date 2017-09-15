(ns daw.events
  (:require [re-frame.core :as re-frame]
            [mozart.audio :as audio]
            [mozart.synth :as synth]))

(re-frame/reg-event-db
  :initialize-db
  (fn  [_ _]
    (let [ctx (audio/create-context!)
          synth (-> (synth/instrument ctx)
                  (synth/add-envelope {:a 0.1 :d 0 :s 1 :r 0.2}))]
      {:ctx ctx
       :synth synth
       :audio-graph (audio/connect {} synth (audio/destination ctx))})))

(re-frame/reg-event-db
  :note-on
  (fn [db [_ note]]
    (update db :synth synth/note-on note)))

(re-frame/reg-event-db
  :note-off
  (fn [db [_ note]]
    (update db :synth synth/note-off note)))

(re-frame/reg-event-db
  :set-wave-type
  (fn [db [_ type]]
    (assoc-in db [:synth :wave-type] type)))

(re-frame/reg-event-db
  :change-envelope
  (fn [db [_ k v]]
    (assoc-in db [:synth :env k] v)))
