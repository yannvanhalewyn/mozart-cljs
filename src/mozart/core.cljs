(ns mozart.audio
  (:require [mozart.graph :as graph]))

(defn- create-context! []
  (let [AudioContext (or js/window.AudioContext js/window.webkitAudioContext)]
    (AudioContext.)))

(defn current-time [ctx] (.-currentTime ctx))
(defn destination [ctx] (.-destination ctx))

;; Audio graph model
;; =================

(defprotocol IConnectable
  "Allows us to define our own nodes types (subgraphs/instruments) to
  enable similar connection as the API nodes and still controll
  internal data functionally"
  (connect* [this to]))

;; Connection for js objects (to be used with generic AudioNodes from
;; the web audio API
(extend-type js/AudioNode
  IConnectable
  (connect* [this to]
    (.connect this to)
    this))

(defn connect [graph node1 node2]
  (connect* node1 node2)
  (graph/connect graph node1 node2))

(defn connect->> [graph & pairs]
  (reduce #(apply connect %1 %2) graph pairs))

;; Instrument
;; ==========

(defn oscillator [ctx type]
  (let [osc (.createOscillator ctx)]
    (set! (.-type osc) type)
    osc))

(defn gain
  ([ctx] (gain ctx 1))
  ([ctx lvl]
   (doto (.createGain ctx)
     (-> .-gain .-value (set! lvl)))))

(defrecord Instrument [ctx vca]
  IConnectable
  (connect* [this to]
    (.connect vca to)
    this))

(defn instrument
  "Returns a connectable instrument node"
  [ctx]
  (Instrument. ctx (gain ctx)))

(defn play! [inst type freq at duration]
  (let [time (-> inst :ctx current-time)]
    (doto (oscillator (.-context (:vca inst)) type)
      (connect* (:vca inst))
      (-> .-frequency (.setValueAtTime freq 0))
      (.start (+ time at))
      (.stop (+ time at duration)))))

(defonce ctx (create-context!))

(def inst (instrument ctx))
(def graph (connect {} inst (destination ctx)))

;; Notes
;; =====

(defn note->freq
  "Converts a music MIDI note number to it's frequency"
  ([note] (note->freq note 440))
  ([note tuning]
   (* (/ tuning 32) (Math/pow 2 (/ (- note 9) 12)))))
