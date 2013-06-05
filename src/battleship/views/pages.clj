(ns battleship.views.pages
  (:require [battleship.views.common :as common])
  (:use [hiccup.core])
  (:use [hiccup.form])
  (:use [noir.core :only [defpage]]))

(def empty-board [["~" "~" "~" "~" "~" "~" "~" "~" "~" "~"]
                  ["~" "~" "~" "~" "~" "~" "~" "~" "~" "~"]
                  ["~" "~" "o" "~" "~" "~" "~" "~" "~" "~"]
                  ["~" "~" "~" "~" "~" "~" "~" "~" "~" "~"]
                  ["~" "~" "~" "x" "/" "~" "~" "~" "~" "~"]
                  ["~" "~" "~" "~" "~" "~" "~" "~" "~" "~"]
                  ["~" "~" "~" "~" "~" "~" "~" "~" "~" "~"]
                  ["~" "~" "~" "~" "~" "~" "~" "~" "~" "~"]
                  ["~" "~" "~" "~" "~" "~" "~" "~" "~" "~"]
                  ["~" "~" "~" "~" "~" "~" "~" "~" "~" "~"]])


(defn make-cell [yCoord xCoord value with-submit?]
  [:td {:name (str (char (+ 65 yCoord)) xCoord) :type (if with-submit? "submit" "") :value value} (str "&nbsp;" value "&nbsp;")]
  )

(defn make-row [yCoord row with-submit?]
  [:tr  [:td (str (char (+ yCoord 65)))](map-indexed (fn [xCoord cell]
                      (make-cell yCoord (+ 1 xCoord) cell with-submit?))
                    row)])

(defn make-board [board id with-submit?]
  (form-to [:post "/"]
           [:h2 "This is the " id " board"]
           [:table {:id id}
            [:tr
              [:th {:style "border: 0px"} "&nbsp;&nbsp;"]
              (for [x (range 1 11)] [:th x])
              ]
            (map-indexed (fn [yCoord row]
                           (make-row yCoord row with-submit?))
                         board)]))

(defn instruction[]
  (html
     [:p#legenda [:h3 "Legenda:"]
                 [:table
                  [:tr [:td {:value "~"} "~"][:td "This is water"]]
                  [:tr [:td {:value "/"} "/"][:td "This means the shot missed"]]
                  [:tr [:td {:value "o"} "o"][:td "This is a part of a ship"]]
                  [:tr [:td {:value "x"} "x"][:td "This is a hit!"]]
                  [:tr [:td {:value "x" :style "background-color: rgb(227, 188, 185);"} "x"][:td "This means you're hit"]]
                  ]]

   ))

;; index page
(defpage "/" []
  (common/layout
   [:h1 "Clojure Project: Epic Battleship"]
   [:div#board (make-board empty-board "axis" true)]
   [:div#right-menu (make-board empty-board "allies" true) (instruction)]
   )
  )
