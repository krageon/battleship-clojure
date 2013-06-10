(ns battleship.views.pages
  (:use [hiccup.core])
  (:use [hiccup.form])
  (:use [hiccup.page :only [include-css include-js html5]])
  (:use [noir.core :only [defpartial]]))

(defn make-cell [yCoord xCoord value with-submit?]
  (if with-submit?
    [:td [:input {:name (str (char (+ 65 yCoord)) xCoord) :type "submit" :value value :title (str (char (+ 65 yCoord)) xCoord)}]]
    [:td {:name (str (char (+ 65 yCoord)) xCoord) :value value :title (str (char (+ 65 yCoord)) xCoord)} (str "&nbsp;" value "&nbsp;")])
  )

(defn make-row [yCoord row with-submit?]
  [:tr  [:td {:class "notboard"}(str (char (+ yCoord 65)))](map-indexed (fn [xCoord cell]
                                                                          (make-cell yCoord (+ 1 xCoord) cell with-submit?))
                                                                        row)])

(defn make-placing-board [board]
  [:h2 "Start placing your fleet!"]
  [:table {:id "fleet"}
   [:tr
    [:th {:style "border: 0px"} "&nbsp;&nbsp;"]
    (for [x (range 1 11)] [:th{:class "header"} x])
    ]
   (map-indexed (fn [yCoord row]
                  (make-row yCoord row true))
                board)])

(defn make-board
  ([board with-submit?] (make-board board "your" with-submit?))
  ([board id with-submit?]
   (form-to {:id "shotboard"}[:post "/"]
            [:h2 "This is " (if (= id "axis") "the ")(if (= id "allies") "the ") id " board"]
            [:table {:id id}
             [:tr
              [:th {:style "border: 0px"} "&nbsp;&nbsp;"]
              (for [x (range 1 11)] [:th{:class "header"} x])
              ]
             (map-indexed (fn [yCoord row]
                            (make-row yCoord row with-submit?))
                          board)])))


;; Partial pages
(defn instruction[]
  (html
   [:p#legenda [:h2 "Legenda:"]
    [:table {:class "notboard"}
     [:tr [:td {:value "~"} "~"][:td "This is water"]]
     [:tr [:td {:value "/"} "/"][:td "This means the shot missed"]]
     [:tr [:td {:value "o"} "o"][:td "This is a part of a ship"]]
     [:tr [:td {:value "x"} "x"][:td "This is a hit!"]]
     [:tr [:td {:value "x" :style "background-color: rgb(227, 188, 185);"} "x"][:td "This means you're hit"]]
     ]]
   ))

(defn fleet [ships]
  (html
   [:p#fleet [:h2 "Fleet:"] "Place your fleet on the board!"
    [:table {:class "notboard"}
     [:thead
      [:tr [:th "#"][:th "ship"][:th "size"][:th "add"]]]
     (into [:tbody]
           (for [ship ships]
             [:tr [:td {:id "amount"} (:amount ship) "x"][:td {:id "name"} (:name ship)][:td {:id "size"} (:size ship)][:td {:class "add" :id (:name str(:name ship "Btn")):style "cursor: pointer"} "+"]]
             )
           )
     ]]
    [:form {:id "fleeter"} [:input {:type "submit" :style "cursor: pointer" :id "fleetsend" :value "To Battle!!"} ]]
   ))

(defn game-over[]
  (html
   [:p#game-over [:h2 "Game Over!"] "You won! Or Lost! I don't know actually.. Figure that out yourself!"]))

;; Layout
(defpartial layout
  [& content]
  (html5
   [:head
    [:title "Clojure Project: Epic Battleship"]
    (include-css "/css/stylesheet.css")
    (include-js "/js/jquery-2.0.2.min.js")
    (include-js "/js/custom.js")]
   [:body
    [:div#wrapper [:h1 "Clojure Project: Epic Battleship"]
     content]]))

;; Start screen
(defn start-screen [allies ships]
  (layout
   [:div#board (make-placing-board allies)]
   [:div#right-menu (fleet ships) (instruction)]
   ))

;; Play screen
(defn play-screen [axis allies]
  (layout
   [:div#board (make-board axis "shot" true)]
   [:div#right-menu (make-board allies false) (instruction)]
   ))

; End screen
(defn end-screen [axis allies]
  (layout
   [:div#board (make-board axis "axis" false)]
   [:div#right-menu (make-board allies false) (game-over)]
   ))