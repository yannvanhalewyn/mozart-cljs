(ns mozart.audio
  (:require [mozart.graph :as graph]))

;; Audio context
;; =============

(defn- create-context!
  "Creats a new audio context. Will use the webkit prefix if needed."
  []
  (let [AudioContext (or js/window.AudioContext js/window.webkitAudioContext)]
    (AudioContext.)))

(defn current-time
  "Returns the current time for the audio context."
  [ctx] (.-currentTime ctx))

(defn destination
  "Returns the destination of the audio context. Most likely the
  computer's speakers, headphones or any connected sound
  cards. Usually the final node in the audio graph."
  [ctx] (.-destination ctx))

;; Audio graph model
;; =================

(defprotocol IConnectable
  "Allows us to define our own nodes types (subgraphs/instruments) to
  enable similar connection as the API nodes and still controll
  internal data functionally"
  (connect* [this to]))

;; Connection for generic AudioNode's from the web audio API
(extend-type js/AudioNode
  IConnectable
  (connect* [this to]
    (.connect this to)
    this))

(defn connect
  "Connects the two nodes internally. Since Web Audio does not allow
  graph introspection, return our own model of the connection graph."
  [graph node1 node2]
  (connect* node1 node2)
  (graph/connect graph node1 node2))

(defn connect->>
  "Takes a collection of connection tuples and connects them together.

  Example:
  `(connect->> graph
     [osc1 gain]
     [osc2 gain]
     [gain destination])`
    => +------+
       | osc1 +-----------+
       +---+--+           |
           |              |
           |              v
           |            +------+     +-------------+
           |            | gain +---> | destination |
           |            +------+     +-------------+
           |              ^
           V              |
       +------+           |
       | osc2 +-----------+
       +------+"
  [graph & pairs]
  (reduce #(apply connect %1 %2) graph pairs))

(defn oscillator
  "Returns a connectable oscillator node. Warning, oscillators must be started!"
  [ctx type]
  (let [osc (.createOscillator ctx)]
    (set! (.-type osc) type)
    osc))

(defn start
  "Starts the oscillator"
  ([osc] (start osc 0))
  ([osc at] (.start osc at) osc))

(defn stop
  "Stops the oscillator"
  ([osc] (stop osc 0))
  ([osc at] (.stop osc at) osc))

(defn gain
  "Returns a connectable gain node."
  ([ctx] (gain ctx 1))
  ([ctx lvl]
   (doto (.createGain ctx)
     (-> .-gain .-value (set! lvl)))))
