(ns mozart.synth
  (:require [mozart.audio :as audio]))

;; Notes
;; =====

(defn note->freq
  "Converts a music MIDI note number to it's frequency"
  ([note] (note->freq note 440))
  ([note tuning]
   (* (/ tuning 32) (Math/pow 2 (/ (- note 9) 12)))))

;; A domain model for a playable instrument. The instrument will keep
;; track of it's vco's and vco's and can be connected to the rest of
;; the audio graph.
(defrecord Instrument [ctx vca vcos wave-type]
  audio/IConnectable
  (connect* [this to]
    (.connect vca to)
    this))

(defn instrument
  "Returns a connectable Instrument node"
  [ctx]
  (Instrument. ctx (audio/gain ctx) nil "sine"))

(defn note-on
  "Creates and starts a vco for the given type and frequency. Returns
  a new instrument with that playing vco."
  ([inst note] (note-on inst note 0))
  ([inst note at]
   (let [time (+ at (-> inst :ctx audio/current-time))
         freq (note->freq note)]
     (if-not (get-in inst [:vcos note])
       (assoc-in inst
         [:vcos note]
         (doto (audio/oscillator (:ctx inst) (:wave-type inst))
           (audio/connect* (:vca inst))
           (-> .-frequency (.setValueAtTime freq time))
           (audio/start time)))
       inst))))

(defn note-off
  "Stops the vco, returns a new instrument without the vco node."
  ([inst note] (note-off inst note 0))
  ([inst note at]
   (if-let [vco (get-in inst [:vcos note])] (audio/stop vco))
   (update inst :vcos dissoc note)))

(defn play!
  "Play a note on the instrument for the given duration"
  [inst note at duration]
  (let [time (-> inst :ctx audio/current-time)]
    (-> inst
      (note-on note (+ time at))
      (note-off note (+ time at duration)))))
