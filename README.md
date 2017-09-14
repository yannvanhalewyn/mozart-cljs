# Mozart

Work in progress. A functional web audio synthesis library using the Web Audio API.

## Why

There are some good Web Audio wrappers, but the goal of Mozart is to allow graph introspection of the audio nodes. This is achieved by modeling all modules and instruments after connectable nodes and keep a reference to the connection graph, thus allowing easy addition and removal of elements.

## Example

The next example will create a sine wave and a square wave, connect both to a vca and connect that vca into the destination (eg: speakers):

``` clojure
(ns my-ns
  (:require [mozart.audio :as audio]
            [mozart.synth :as synth]))

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
