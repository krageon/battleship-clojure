(ns battleship.views.welcome
  (:require [battleship.views.common :as common])
  (:use [noir.core :only [defpage]]))

(defn createBoard[]
  "Returns a clean board"
  (str
     "<table>"
     (reduce str(repeat 11(str "<tr>"(reduce str(repeat 11 "<td>&nbsp;</td>")) "</tr>")))
     "</table>"
   )
  )

(defpage "/" []
  (common/layout
   [:h1 "Clojure Project: Epic Battleship"]
   [:div#board (createBoard)]))
