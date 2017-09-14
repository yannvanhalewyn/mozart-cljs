(ns mozart.synth
  (:require [mozart.audio :as audio]))

;; A domain model for a playable instrument. The instrument will keep
;; track of it's vco's and vco's and can be connected to the rest of
;; the audio graph.
(defrecord Instrument [ctx vca vco]
  audio/IConnectable
  (connect* [this to]
    (.connect vca to)
    this))

(defn instrument
  "Returns a connectable Instrument node"
  [ctx]
  (Instrument. ctx (audio/gain ctx) nil))

(defn note-on
  "Creates and starts a vco for the given type and frequency. Returns
  a new instrument with that playing vco."
  ([inst type freq] (note-on inst type freq 0))
  ([inst type freq at]
   (let [time (+ at (-> inst :ctx audio/current-time))]
     (assoc inst
       :vco
       (doto (audio/oscillator (:ctx inst) type)
         (audio/connect* (:vca inst))
         (-> .-frequency (.setValueAtTime freq time))
         (.start time))))))

(defn note-off
  "Stops the vco, returns a new instrument without the vco node."
  ([inst] (note-off inst 0))
  ([inst at]
   (if-let [vco (:vco inst)] (.stop vco at))
   (dissoc inst :vco)))

(defn play!
  "Play a note on the instrument for the given duration"
  [inst type freq at duration]
  (let [time (-> inst :ctx audio/current-time)]
    (-> inst
        (note-on type freq (+ time at))
        (note-off (+ time at duration)))))

;; Notes
;; =====

(defn note->freq
  "Converts a music MIDI note number to it's frequency"
  ([note] (note->freq note 440))
  ([note tuning]
   (* (/ tuning 32) (Math/pow 2 (/ (- note 9) 12)))))
