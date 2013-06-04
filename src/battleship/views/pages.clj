(ns battleship.views.pages
  (:require [battleship.views.common :as common])
  (:use [hiccup.core])
  (:use [noir.core :only [defpage]]))

;; functions
(defn createBoard[]
  "Returns a board, [] == Empty, [coordinateAction] == [Y X Action]"
  []
  (html [:table
         [:tr
          [:th "&nbsp;&nbsp;"]
          (for [x (range 1 11)] [:th x])
          ]
         (for [y (map char(range 65 75))] [:tr
                                           [:th y]
                                           (for [x (range 1 11)] [:td {:id (str y x) } "&nbsp;&nbsp;"])
                                           ])
         ])
  )

;; index page
(defpage "/" []
  (common/layout
   [:h1 "Clojure Project: Epic Battleship"]
   [:div#board (createBoard)]
   )
  )



