(ns daw.events
  (:require [re-frame.core :as re-frame]
            [mozart.audio :as audio]
            [mozart.synth :as synth]))

(defonce ctx (audio/create-context!))
(def inst (atom (synth/instrument ctx)))

(def graph (audio/connect {} @inst (audio/destination ctx)))

(re-frame/reg-fx
 :note-on
 (fn [note]
   (swap! inst synth/note-on "sine" (synth/note->freq note))))

(re-frame/reg-fx
 :note-off
 (fn [note] (swap! inst synth/note-off)))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   {}))

(re-frame/reg-event-fx
 :note-on
 (fn [db [_ note]]
   {:note-on note}))

(re-frame/reg-event-fx
 :note-off
 (fn [db [_ note]]
   {:note-off note}))
