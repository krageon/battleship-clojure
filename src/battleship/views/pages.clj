(ns battleship.views.pages
  (:require [battleship.views.common :as common])
  (:use [hiccup.core])
  (:use [noir.core :only [defpage]]))

(defn cell-html[yCoord xCoord value withSubmit?]
  [:td {:name (str yCoord xCoord) :type (if withSubmit? "submit" "") :value value} (str "&nbsp;" value "&nbsp;")]
  )

(defn createBoard[]
  "Returns a clean table board"
  []
  (html [:table
         [:tr
          [:th {:style "border: 0px"} "&nbsp;&nbsp;"]
          (for [x (range 1 11)] [:th x])
          ]
         (for [y (map char(range 65 75))] [:tr
                                           [:th y]
                                           (for [x (range 1 11)] (cell-html y x "~" true))
                                           ])
         ])
  )

(defn makeMove
  "Give Coordinates and the action, and it will set it."
  [params]
  (let[x (second params), y (first params), action (last params)]
    into [:td#A1 action])
  )




;; index page
(defpage "/" []
  (common/layout
   [:h1 "Clojure Project: Epic Battleship"]
   [:div#board (createBoard)]
   )
  )



