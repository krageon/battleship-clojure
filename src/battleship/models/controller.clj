(ns battleship.models.controller
  (:use compojure.core)
  (:use [noir.core :only [defpage]])
  (:use [clojure.string :only [join split]])
  (:require  [noir.cookies :as cookies]
             [compojure.core :as compojure]
             [battleship.views.pages :as view]
             [compojure.route :as route]
             [battleship.models.core :as model]
             [noir.session :as session]))

; persistence
(defn save-key [k v]
  (println "save" k v)
  (cookies/put! k (str v)))

(defn load-key [k]
  (println "load" k)
  (read-string (cookies/get k)))

(defn log [x]
  (println x)
  x)

(defn blank-player [] {:board {},
                       :shot {}
                       :ships []})

(defn get-board [player]
  (let [board (if (= player "allies")
                (model/bs-board (:board (load-key :allies)))
                (model/bs-board (:board (load-key :axis))))]
    board)
  )

(defn put-ship [ship xy horizontal] ; (fn [player ship orientation coordinates]
  (let [coord-fixed (model/coord-display-to-backend xy)]
    (println "asd" ship coord-fixed)
    (save-key :allies (model/bs-ship-put (load-key :allies) ship horizontal coord-fixed))))

(defn reset-game! []
  (do
    (save-key :axis (model/ai-setup (blank-player)))
    (save-key :allies (blank-player))))

(defn start-page []
  (do
    (reset-game!)
    (view/start-screen (get-board "allies") model/ships)))

(defn ai-shoot []
  (do
    (let [result (model/ai-shoot (load-key :axis) (load-key :allies))]
      (save-key :allies (result 1))
      (save-key :axis (result 0)))
    (if (model/have-won? (load-key :axis) (load-key :allies))
      (view/end-screen (get-board "axis") (get-board "allies"))
      (view/play-screen (get-board "axis") (get-board "allies")))))

(defn shoot [coordinates] ; "A6"
  (do
    (let [result (model/bs-shoot (model/coord-display-to-backend coordinates) (load-key :allies) (load-key :axis))]
      (save-key :allies (result 0))
      (save-key :axis (result 1)))
    (if (model/have-won? (load-key :allies) (load-key :axis))
      (view/end-screen)
      (ai-shoot))))

(defpage [:get "/"] {} (start-page))
(defpage [:post "/ships"] {:keys [name xy horizontal]} (put-ship name xy horizontal))
(defpage [:get "/play"] {} (view/play-screen (get-board "axis")(get-board "allies")))
(defpage [:post "/"] {:keys [xy]}
  (if (nil? xy)
    (view/play-screen (get-board "axis") (get-board "allies"))
    (shoot xy)))