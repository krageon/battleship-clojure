;; Battleship in clojure core

;; Printing the board

; constants
(def default "~")
(def boat "o")
(def hit "*")
(def shot "x")
(def new-line "\n")

(def bs-player-a (atom {:board {}, :shot {}}))
(def bs-player-b (atom {:board {}, :shot {}}))

(def bs-cell-get
  (fn [board coordinates]
    (let [at-coordinates (@board coordinates)]
      (if (nil? at-coordinates)
        default
        at-coordinates))))

; converting a board (a sparse array-thing) to a string, filling in the gaps
(def bs-board-string
  (fn [board]
    (loop [x 0
           maxX 10
           y 0
           maxY 10
           string ""]
      (if (> y maxY)
        string
        (if (> x maxX)
          (recur 0 maxX (inc y) maxY (str string new-line))
          (recur (inc x) maxX y maxY (str string (bs-cell-get board [x y]))))
        )
      )
    ))

;; Picking ship locations
(def ships [{:name "aircraft carrier", :size 5, :amount 1},
            {:name "battleship", :size 4, :amount 1},
            {:name "cruiser", :size 3, :amount 1},
            {:name "destroyer", :size 2, :amount 2},
            {:name "submarine", :size 1, :amount 2}])


;; Making a move
(def bs-shoot
  (fn [player]
    ))

;; Randomised ship setup

