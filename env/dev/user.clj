(ns user
  (:require [figwheel-sidecar.repl-api :as ra]))

(def start ra/start-figwheel!)
(def stop ra/stop-figwheel!)
(def cljs-repl ra/cljs-repl)
