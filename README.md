# Mozart

Work in progress. A functional web audio synthesis library using the Web Audio API.

## Why

There are some good Web Audio wrappers, but the goal of Mozart is to allow graph introspection of the audio nodes. This is achieved by modeling all modules and instruments after connectable nodes and keep a reference to the connection graph, thus allowing easy addition and removal of elements.

## Playing Oscillators

The next example will create a sine wave and a square wave, connect both to a vca and connect that vca into the destination (eg: speakers):

``` clojure
(ns my-ns
  (:require [mozart.audio :as audio]

(def ctx (audio/create-context!))
(def gain (audio/gain ctx))
(def osc1 (audio/oscillator ctx "sine"))
(def osc2 (audio/oscillator ctx "square"))

(def graph
  (audio/connect->> {}
    [osc1 gain]
    [osc2 gain]
    [gain (audio/destination ctx)]))

(audio/start osc1)
(audio/start osc2)
```

## Modeling a synth

This is the declarative API this library is striving for. This will
allow you to create modular synthesis at runtime.

``` clojure
(ns my-ns
  (:require [mozart.synth :as synth]
            [mozart.audio :as audio))

(def ctx (audio/create-context!))

(def keyboard (inputs/keyboard))

(def synth
  (let [osc1 (synth/oscillator "sine")
        osc2 (synth/oscillator "square" {:detune -0.05})
        env  (synth/envelope 0.1 0 1 0.1)]
    (-> (synth/instrument ctx)
      (synth/set-voices 3) ;; Creates a polyphonic synthesizer with 3 voices
      (synth/connect->>
        [keyboard osc1]
        [keyboard osc2]
        [keyboard env] ;; The envelope is triggered by the keyboard, just like the oscillators
        [env osc1]
        [env osc2]))))

;; #mozart.synth/Instrument
;; {:ctx ctx
;;  :vcos (osc1 osc2)
;;  :max-voices 3
;;  :vca internal-vca
;;  :voices { } ;; map of notes -> [osc voice] tuples
;;  :graph {osc1 (internal-vca)
;;          osc2 (internal-vca)
;;          env (osc1 osc2)}}

(def graph (audio/connect synth (audio/destination ctx)))

(synth/note-on! synth 440)
```
