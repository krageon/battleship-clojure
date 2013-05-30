;; Battleship in clojure core

;; Printing the board

(def bs-board-test {[0 0] "*"})

(def bs-board-atom (atom bs-board-test))

(def bs-cell-get
  (let [default "~"]
    (fn [coordinates]
      (let [at-coordinates (@bs-board-atom coordinates)]
        (if (nil? at-coordinates)
          default
          at-coordinates)))))

(def bs-board-string
  (fn [] (loop [x 0
                maxX 20
                y 0
                maxY 20
                string ""]
           (if (> y maxY)
             string
             (if (> x maxX)
               (recur 0 maxX (inc y) maxY (str string "\n"))
               (recur (inc x) maxX y maxY (str string (bs-cell-get [x y]))))
             )
           )
    ))

(println (bs-board-string))

;; Picking ship locations


;; Randomised ship setup


;; Making a move

