(ns dev.user
  (:require [daw.core :as app]))

(defonce on-js-load app/mount-root)

(app/init!)
