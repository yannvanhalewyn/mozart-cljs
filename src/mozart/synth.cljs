(ns mozart.synth
  (:require [mozart.audio :as audio]))

;; Notes
;; =====

(defn note->freq
  "Converts a music MIDI note number to it's frequency"
  ([note] (note->freq note 440))
  ([note tuning]
   (* (/ tuning 32) (Math/pow 2 (/ (- note 9) 12)))))

;; Voices
;; ======

(defrecord Voice [vco vca graph]
  audio/IConnectable
  (connect* [this to]
    (update this :graph audio/connect vca to)))

(defn voice [ctx freq type]
  (let [vco (doto (audio/oscillator ctx type)
              (-> .-frequency (.setValueAtTime freq 0)))
        vca (audio/gain ctx 0)]
    (->Voice vco vca (audio/connect {} vco vca))))

(defn start-voice [voice at {:keys [a d s r]}]
  (let [target (-> voice :vca .-gain)]
    (.cancelScheduledValues target at)
    (.linearRampToValueAtTime target 1 (+ at a))
    (.linearRampToValueAtTime target s (+ at a d)))
  (update voice :vco audio/start at))

(defn stop-voice [voice at env]
  (let [target (-> voice :vca .-gain)]
    (.cancelScheduledValues target 0)
    (.linearRampToValueAtTime target 0 (+ at (:r env))))
  (update voice :vco audio/stop (+ at (-> env :r) 0.2)))

;; A domain model for a playable instrument. The instrument will keep
;; track of it's voices and vca and can be connected to the rest of
;; the audio graph.
(defrecord Instrument [ctx vca voices wave-type graph env]
  audio/IConnectable
  (connect* [this to]
    (update this :graph audio/connect vca to)))

(defn instrument
  "Returns a connectable Instrument node"
  [ctx]
  (map->Instrument {:ctx ctx
                    :vca (audio/gain ctx)
                    :wave-type "sine"
                    :graph {}}))

(defn note-on
  "Creates and starts a vco for the given type and frequency. Returns
  a new instrument with that playing vco."
  ([inst note] (note-on inst note 0))
  ([inst note at]
   (if-not (get-in inst [:voices note])
     (let [time (+ at (-> inst :ctx audio/current-time))
           freq (note->freq note)
           voice (-> (voice (:ctx inst) freq (:wave-type inst))
                   (audio/connect* (:vca inst))
                   (start-voice time (:env inst)))]
       (assoc-in inst [:voices note] voice))
     inst)))

(defn note-off
  "Stops the vco, returns a new instrument without the vco node."
  ([inst note] (note-off inst note (-> inst :vca .-context audio/current-time)))
  ([inst note at]
   (when-let [voice (get-in inst [:voices note])]
     (stop-voice voice at (:env inst)))
   (update inst :voices dissoc note)))

(defn play!
  "Play a note on the instrument for the given duration"
  [inst note at duration]
  (let [time (-> inst :ctx audio/current-time)]
    (-> inst
      (note-on note (+ time at))
      (note-off note (+ time at duration)))))

(defn add-envelope [inst adsr]
  (assoc inst :env adsr))
