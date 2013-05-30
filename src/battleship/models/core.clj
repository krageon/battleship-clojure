;; Battleship in clojure core

;; Printing the board

; constants
(def default "~")
(def boat "o")
(def hit "x")
(def shot "/")
(def new-line "\n")

(def bs-player-a (atom {:board {}, :shot {}}))
(def bs-player-b (atom {:board {}, :shot {}}))

(def bs-cell-get
  (fn [board coordinates]
    (let [at-coordinates (board coordinates)]
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

(defn bs-ship-get
  [n] (first (filter #(= n (:name %)) ships)))

(defn bs-ship-coordinate-offset [orientation coordinates offset]
  (if (= "horizontal" orientation)
    [(+ (get coordinates 0) offset) (get coordinates 1)]
    [(get coordinates 0) (+ (get coordinates 1) offset)]))

(defn bs-ship-put-is-legal [player ship orientation coordinates]
  (loop [offset 0]
    (if-not (>= offset (:size (bs-ship-get ship)))
      (if (= default (bs-cell-get (:board @player) (bs-ship-coordinate-offset orientation coordinates offset)))
        (recur (inc offset))
        (do (println "false") false))
      true))
  )

(def bs-ship-put
  (fn [player ship orientation coordinates] ; coordinates are top or left, respectively
    (if (bs-ship-put-is-legal player ship orientation coordinates)
      (do
        (loop [offset 0]
          (if-not (>= offset (:size (bs-ship-get ship)))
            (do
              (swap! player #(assoc-in % [:board (bs-ship-coordinate-offset orientation coordinates offset)]
                                       boat))
              (recur (inc offset)))
            ))
        true)
      false)))

(do
  (bs-ship-put bs-player-a "battleship" "horizontal" [0 1])
  (bs-ship-put bs-player-a "battleship" "vertical" [0 0]))

(println (bs-board-string (:board @bs-player-a)))

;; Making a move
(def bs-shoot
  (fn [coordinates player opponent]
    (swap! player
           #(assoc-in % [:shot coordinates]
                      (if (= boat (bs-cell-get (:board @opponent)))
                        hit
                        shot)))))

;; Randomised ship setup

