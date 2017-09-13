(ns daw.audio
  (:require [daw.graph :as graph]))

(defn- create-context! []
  (let [AudioContext (or js/window.AudioContext js/window.webkitAudioContext)]
    (AudioContext.)))

(defn current-time [ctx] (.-currentTime ctx))
(defn destination [ctx] (.-destination ctx))

(defn oscillator [ctx type at duration]
  (let [osc (.createOscillator ctx)
        time (current-time ctx)]
    (doto osc
      (-> .-type (set! type))
      (.start (+ at time))
      (.stop (+ time at duration)))))

(defn gain
  ([ctx] (gain ctx 1))
  ([ctx lvl]
   (doto (.createGain ctx)
     (-> .-gain .-value (set! lvl)))))

(defn connect [graph node1 node2]
  (.connect node1 node2)
  (graph/connect graph node1 node2))

(defn connect->> [graph & pairs]
  (reduce #(apply connect %1 %2) graph pairs))
