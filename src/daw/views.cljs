(ns daw.views
  (:require [re-frame.core :as rf]))

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

(defn osc-panel []
  [:div
   [:select {:on-change #(rf/dispatch [:set-wave-type (.. % -target -value)])}
    [:option {:value "sine"} "sine"]
    [:option {:value "square"} "square"]
    [:option {:value "saw"} "saw"]
    [:option {:value "triangle"} "triangle"]]])

(defn keyboard
  "Returns a keyboard component. Notes is a vector of MIDI note
  numbers to display."
  [{:keys [notes on-note-down on-note-up]}]
  [:ul.keyboard
   (for [note notes]
     ^{:key note}
     [:li.key {:class (get key-classes (mod note 12))
               :on-mouse-down (partial on-note-down note)
               :on-mouse-up (partial on-note-up note)}])])

(defn main-panel []
  [:div
   [osc-panel]
   [keyboard {:notes (range 60 85)
              :on-note-down #(rf/dispatch [:note-on %])
              :on-note-up #(rf/dispatch [:note-off %])}]])
