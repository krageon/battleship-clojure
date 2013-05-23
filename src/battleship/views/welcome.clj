(ns battleship.views.welcome
  (:require [battleship.views.common :as common]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage]]))

(defpage "/welcome" []
  (common/layout
   [:h1 "Some text here :3"]
   [:p "Welcome to battleship"]))
