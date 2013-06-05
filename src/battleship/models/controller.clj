(ns battleship.models.controller
  (:use compojure.core)
  (:use [noir.core :only [defpage]])
  (:require  [compojure.core :as compojure]
             [battleship.views.pages :as view]
             [compojure.route :as route]
             ;[battleship.models.core :as model]
             ))

(defn start-page[]
  ;;  (model/reset-game!)
  (view/start-screen))

(defn shoot [coordinates]
  (view/test-screen))

(defpage [:get "/"] [] (start-page))
(defpage [:post "/"] [] (shoot {}))


