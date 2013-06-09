(ns battleship.models.controller
  (:use compojure.core)
  (:use [noir.core :only [defpage]])
  (:require  [noir.cookies :as cookies]
             [compojure.core :as compojure]
             [battleship.views.pages :as view]
             [compojure.route :as route]
             [battleship.models.core :as model]))

;; Tim TODO:
; Een functie maken die de schepen die ik meegeef in de state zet (allies board)
; Vanuit de gui geef ik dit mee:
; input: (defn ships [] [{:name "Aircraft Carrier" :xy "B6" :horizontal true}
;                        {:name "Submarine" :xy "A1" :horizontal false}] etc... )
;
; Een functie die een schot registreert, checkt of iemand gewonnen heeft en anders
; de computer een zet laat doen
; input: (defn shoot [coordinates]) ;;=> "A6"

;(def bs-player-a {:board {},
                  ;:shot {},
                  ;:ships []})
(defn blank-player [] {:board {},
                    :shot {}
                    :ships []})

(defn reset-game! []
  (do
    (cookies/put! :axis (blank-player))
    (cookies/put! :allies (blank-player))
    ))

(defn start-page []
  (reset-game!)
  (view/start-screen))

(defn get-board [player]
  (if (= player "allies")
    (model/bs-board (:board (cookies/get :allies)))
    (model/bs-board (:board (cookies/get :axis)))))

(defn shoot [coordinates] ; "A6"
  (cookies/put! :allies (model/bs-shoot (model/coord-display-to-backend coordinates) (cookies/get :allies) (cookies/get :axis))))



(defpage [:get "/"] [] (start-page))
(defpage [:post "/"] [] (shoot {}))


