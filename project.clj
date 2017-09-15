(defproject mozart-cljs "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0-alpha20"]
                 [org.clojure/clojurescript "1.9.908"]
                 [reagent "0.8.0-alpha1"]
                 [re-frame "0.10.1"]]

  :plugins [[lein-cljsbuild "1.1.5"]]

  :min-lein-version "2.5.3"

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.4"]
                                  [figwheel-sidecar "0.5.13"]
                                  [org.clojure/tools.nrepl "0.2.13"]
                                  [com.cemerick/piggieback "0.2.2-SNAPSHOT"]]
                   :source-paths ["src" "env/dev"]
                   :plugins      [[lein-figwheel "0.5.13"]]}}

  :cljsbuild {:builds
              [{:id           "dev"
                :source-paths ["src" "env/dev"]
                :figwheel     {:on-jsload "dev.user/on-js-load"}
                :compiler     {:main                 dev.user
                               :output-to            "resources/public/js/compiled/app.js"
                               :output-dir           "resources/public/js/compiled/out"
                               :asset-path           "js/compiled/out"
                               :pretty-print         true
                               :source-map-timestamp true
                               :optimizations        :none
                               :preloads             [devtools.preload]
                               :external-config      {:devtools/config {:features-to-install [:formatters]}}}}

               {:id           "prod"
                :source-paths ["src"]
                :compiler     {:main            daw.core
                               :output-to       "resources/public/js/compiled/app.js"
                               :optimizations   :advanced
                               :closure-defines {goog.DEBUG false}
                               :pretty-print    false}}]})
