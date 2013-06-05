(ns battleship.models.controller
  (:use compojure.core)
  (:use [noir.core :only [defpage]])
  (:require  [compojure.core :as compojure]
             [battleship.views.pages :as view]
             [compojure.route :as route]
             ;[battleship.models.core :as model]
             ))

;; Tim TODO:
; Een functie maken die de schepen die ik meegeef in de state zet (allies board)
; Vanuit de gui geef ik dit mee:
; input: (defn ships [] [{:name "Aircraft Carrier" :xy "B6" :horizontal true}
;                        {:name "Submarine" :xy "A1" :horizontal false}] etc... )
;
; Een functie die een schot registreert, checkt of iemand gewonnen heeft en anders
; de computer een zet laat doen
; input: (defn shoot [coordinates]) ;;=> "A6"

(defn start-page[]
  ;;  (model/reset-game!)
  (view/start-screen))

(defn shoot [coordinates]
  (view/test-screen))

(defpage [:get "/"] [] (start-page))
(defpage [:post "/"] [] (shoot {}))


