(ns battleship.views.welcome
  (:require [battleship.views.common :as common])
  (:use [hiccup.core])
  (:use [noir.core :only [defpage]]))

(defn createBoard[]
  "Returns a clean board"
  (html [:table
         [:tr
          [:th "&nbsp;&nbsp;"]
          (for [x (range 1 11)] [:th x])
         ]
         (for [x (map char(concat (range 65 75)))] [:tr [:th x] (for [x (range 10)] [:td "&nbsp;&nbsp;"])])
        ])
 )

(defpage "/" []
  (common/layout
   [:h1 "Clojure Project: Epic Battleship"]
   [:div#board (createBoard)]))
