(ns daw.views
  (:require [re-frame.core :as rf]
            [re-frame.core :as re-frame]))

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

(defn slider [attr value max label]
  [:div
   [:label {:style {:color "white"}} label]
   [:input {:type "range"
            :value (/ (* value 100) max)
            :on-change
            #(re-frame/dispatch
               [:change-envelope attr (-> (js/Number (.. % -target -value))
                                        (* max)
                                        (/ 100))])}]])

(defn osc-panel []
  (let [synth @(re-frame/subscribe [:synth])
        env (:env synth)]
    [:div
     [:select {:on-change #(rf/dispatch [:set-wave-type (.. % -target -value)])}
      [:option {:value "sine"} "sine"]
      [:option {:value "square"} "square"]
      [:option {:value "sawtooth"} "saw"]
      [:option {:value "triangle"} "triangle"]]
     [:br]
     [slider :a (:a env) 3 "A"]
     [slider :r (:r env) 3 "R"]
     [slider :d (:d env) 3 "D"]
     [slider :s (:s env) 1 "S"]]))

(defn keyboard
  "Returns a keyboard component. Notes is a vector of MIDI note
  numbers to display."
  [{:keys [notes playing-notes on-note-down on-note-up]}]
  [:ul.keyboard
   (for [note notes]
     ^{:key note}
     [:li.key {:class (str (get key-classes (mod note 12))
                        (when (playing-notes note) " active"))
               :on-mouse-down (partial on-note-down note)
               :on-mouse-up (partial on-note-up note)}])])

(defn main-panel []
  [:div
   [osc-panel]
   [keyboard {:notes (range 60 85)
              :on-note-down #(rf/dispatch [:note-on %])
              :on-note-up #(rf/dispatch [:note-off %])
              :playing-notes @(rf/subscribe [:playing-notes])}]])
