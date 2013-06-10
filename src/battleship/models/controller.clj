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
(defn blank-player [] {:board {},
                       :shot {}
                       :ships []})

(defn put-ship [ship xy horizontal] ; (fn [player ship orientation coordinates]
  (cookies/put! :allies (model/bs-ship-put (cookies/get :allies) ship (model/coord-display-to-backend xy) horizontal)))

(defn reset-game! []
  (do
    (cookies/put! :axis (blank-player)) ; TODO: Initialize AI ships
    (cookies/put! :allies (blank-player))
    ))

(defn start-page []
  (reset-game!)
  (view/start-screen))

(defn get-board [player]
  (if (= player "allies")
    (model/bs-board (:board (cookies/get :allies)))
    (model/bs-board (:board (cookies/get :axis)))))

(defn ai-move []
  (do
    (cookies/put! :axis (model/ai-move (cookies/get :axis) (cookies/get :allies)))
    (if (model/have-won? (cookies/get :axis))
      (view/end-screen)
      (view/play-screen))))

(defn shoot [coordinates] ; "A6"
  (do
    (cookies/put! :allies (model/bs-shoot (model/coord-display-to-backend coordinates) (cookies/get :allies) (cookies/get :axis)))
    (if (model/have-won? (cookies/get :allies))
      (view/end-screen)
      (ai-move))))

(defpage [:get "/"] [] (start-page))
(defpage [:post "/"] {:keys [xy]} (shoot xy))
(defpage [:post "/ships"] {:keys [ship xy horizontal]} (put-ships ship xy horizontal)