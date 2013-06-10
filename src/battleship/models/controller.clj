(ns battleship.models.controller
  (:use compojure.core)
  (:use [noir.core :only [defpage]])
  (:use '[clojure.string :only [join split]])
  (:require  [noir.cookies :as cookies]
             [compojure.core :as compojure]
             [battleship.views.pages :as view]
             [compojure.route :as route]
             [battleship.models.core :as model]))

; persistence layer notes
; keyword = key to string
; name = string to key

; Een functie die een schot registreert, checkt of iemand gewonnen heeft en anders
; de computer een zet laat doen
; input: (defn shoot [coordinates]) ;;=> "A6"
(defn blank-player [] {:board {},
                       :shot {}
                       :ships []})

(defn save-key [k v]
  (cookies/put! k (str v)))

(defn load-key [k]
  (read-string (cookies/get k)))

(defn get-board [player]
  (if (= player "allies")
    (model/bs-board (:board (load-key :allies)))
    (model/bs-board (:board (load-key :axis)))))

(defn put-ship [ship xy horizontal] ; (fn [player ship orientation coordinates]
  (save-key :allies (model/bs-ship-put (load-key :allies) ship (model/coord-display-to-backend xy) horizontal)))

(defn reset-game! []
  (do
    (save-key :axis (blank-player)) ; TODO: Initialize AI ships
    (save-key :allies (blank-player))))

(defn start-page []
  (do
    (reset-game!)
    (view/start-screen (get-board "allies"))))

(defn ai-move []
  (do
    (save-key :axis (model/ai-move (load-key :axis) (load-key :allies)))
    (if (model/have-won? (load-key :axis) (load-key :allies))
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