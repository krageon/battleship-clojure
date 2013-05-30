;; Battleship in clojure core

;; Printing the board

; constants
(def default "~")
(def boat "o")
(def hit "*")
(def new-line "\n")

(def bs-board-a (atom {}))
(def bs-board-b (atom {}))

(def bs-cell-get
  (fn [board coordinates]
    (let [at-coordinates (@board coordinates)]
      (if (nil? at-coordinates)
        default
        at-coordinates))))

; converting the board (a sparse array-thing) to a string, filling in the gaps
(def bs-board-string
  (fn [board]
    (loop [x 0
           maxX 20
           y 0
           maxY 20
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


;; Randomised ship setup


;; Making a move

