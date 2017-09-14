(ns daw.views
  (:require [re-frame.core :as re-frame]))

(def key-classes
  ["key--white"                    ;; C
   "key--black"                    ;; C#
   "key--white key--white-center"  ;; D
   "key--black"                    ;; D#
   "key--white key--white-right"   ;; E
   "key--white"                    ;; F
   "key--black"                    ;; F#
   "key--white key--white-center"  ;; G
   "key--black"                    ;; G#
   "key--white key--white-center"  ;; A
   "key--black"                    ;; A#
   "key--white key--white-right"]) ;; B

(defn keyboard [{:keys [notes]}]
  [:ul.keyboard
   (for [note notes]
     [:li.key {:class (get key-classes (mod note 12))}])])

(defn main-panel []
  [keyboard {:notes (range 60 85)}])
