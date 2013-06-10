(ns battleship.models.controller
  (:use compojure.core)
  (:use [noir.core :only [defpage]])
  (:use [clojure.string :only [join split]])
  (:require  [noir.cookies :as cookies]
             [compojure.core :as compojure]
             [battleship.views.pages :as view]
             [compojure.route :as route]
             [battleship.models.core :as model]))

; persistence
(defn save-key [k v]
  (cookies/put! k (str v)))

(defn load-key [k]
  (read-string (cookies/get k)))

; Een functie die een schot registreert, checkt of iemand gewonnen heeft en anders
; de computer een zet laat doen
; input: (defn shoot [coordinates]) ;;=> "A6"
(defn blank-player [] {:board {},
                       :shot {}
                       :ships []})

(defn get-board [player]
  (if (= player "allies")
    (model/bs-board (:board (load-key :allies)))
    (model/bs-board (:board (load-key :axis)))))

(defn put-ship [ship xy horizontal] ; (fn [player ship orientation coordinates]
  (let [coord-fixed (model/coord-display-to-backend xy)]
    (do
      (println (load-key :allies) ship coord-fixed horizontal)
      (save-key :allies (model/bs-ship-put (load-key :allies) ship coord-fixed horizontal)))))

(defn reset-game! []
  (do
    (save-key :axis (blank-player)) ; TODO: Initialize AI ships
    (save-key :allies (blank-player))))

(defn start-page []
  (do
    (reset-game!)
    (view/start-screen (get-board "allies") model/ships)))

(defn ai-move []
  (do
    (save-key :axis (model/ai-move (load-key :axis) (load-key :allies)))
    (if (model/have-won? (load-key :axis) (load-key :allies))
      (view/end-screen (get-board "axis") (get-board "allies"))
      (view/play-screen (get-board "axis") (get-board "allies")))))

(defn shoot [coordinates] ; "A6"
  (do
    (save-key :allies (model/bs-shoot (model/coord-display-to-backend coordinates) (load-key :allies) (load-key :axis)))
    (if (model/have-won? (load-key :allies) (load-key :axis))
      (view/end-screen)
      (ai-move))))

(defpage [:get "/"] {} (start-page))
;(view/play-screen (get-board "axis") (get-board "allies"))
(defpage [:post "/"] {:keys [xy]}
    (shoot xy))
(defpage [:post "/ships"] {:keys [name xy horizontal]} (put-ship name xy horizontal))