(ns battleship.models.controller
  (:use compojure.core)
  (:use [noir.core :only [defpage]])
  (:require  [noir.cookies :as cookies]
             [compojure.core :as compojure]
             [battleship.views.pages :as view]
             [compojure.route :as route]
             [battleship.models.core :as model]))

; Een functie die een schot registreert, checkt of iemand gewonnen heeft en anders
; de computer een zet laat doen
; input: (defn shoot [coordinates]) ;;=> "A6"
(defn blank-player [] {:board {},
                       :shot {}
                       :ships []})

(defn get-board [player]
  (if (= player "allies")
    (model/bs-board (:board (cookies/get :allies)))
    (model/bs-board (:board (cookies/get :axis)))))

(defn put-ship [ship xy horizontal] ; (fn [player ship orientation coordinates]
  (cookies/put! :allies (model/bs-ship-put (cookies/get :allies) ship (model/coord-display-to-backend xy) horizontal)))

(defn reset-game! []
  (do
    (cookies/put! :axis (blank-player)) ; TODO: Initialize AI ships
    (cookies/put! :allies (blank-player))
    ))

(defn start-page []
  (reset-game!)
  (view/start-screen (get-board "axis")))

(defn ai-move []
  (do
    (cookies/put! :axis (model/ai-move (cookies/get :axis) (cookies/get :allies)))
    (if (model/have-won? (cookies/get :axis) (cookies/get :allies))
      (view/end-screen (get-board "axis") (get-board "allies"))
      (view/play-screen (get-board "axis") (get-board "allies")))))

(defn shoot [coordinates] ; "A6"
  (do
    (cookies/put! :allies (model/bs-shoot (model/coord-display-to-backend coordinates) (cookies/get :allies) (cookies/get :axis)))
    (if (model/have-won? (cookies/get :allies) (cookies/get :axis))
      (view/end-screen)
      (ai-move))))

(defpage [:get "/"] [] (start-page))
(defpage [:post "/"] {:keys [xy]} (shoot xy))
(defpage [:post "/ships"] {:keys [ship xy horizontal]} (put-ship ship xy horizontal))