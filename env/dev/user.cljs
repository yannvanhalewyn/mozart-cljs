(ns dev.user
  (:require [daw.core :as app]))

(defonce on-js-load (do (app/init!) app/mount-root))
