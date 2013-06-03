(ns battleship.views.welcome
  (:require [battleship.views.common :as common])
  (:use [noir.core :only [defpage]]))

(defpage "/" []
  (common/layout
   [:h1 "Lamamamama"]
   [:p "Hele zinnige  tekst"]))


(defpage "/welcome" []
  (common/layout
   [:h1 "Some text here :3"]
   [:p "Welcome to battleship"]))
