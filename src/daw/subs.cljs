(ns daw.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
  :playing-notes
  (fn [db]
    (set (-> db :synth :voices keys))))

(re-frame/reg-sub :synth :synth)
