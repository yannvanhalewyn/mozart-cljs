(ns daw.views
  (:require [re-frame.core :as re-frame]))

(defn keyboard []
  [:ul.keyboard
   [:li.key--white.key]
   [:li.key--black.key]
   [:li.key--white.key--white-center.key]
   [:li.key--black.key]
   [:li.key--white.key--white-right.key]

   [:li.key--white.key]
   [:li.key--black.key]
   [:li.key--white.key--white-center.key]
   [:li.key--black.key]
   [:li.key--white.key--white-center.key]
   [:li.key--black.key]
   [:li.key--white.key--white-right.key]
   [:li.key--white.key]])

(defn main-panel []
  [keyboard])
