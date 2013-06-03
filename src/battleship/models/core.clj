;; Battleship in clojure core

;; Printing the board

; constants
(def default "~")
(def boat "o")
(def hit "x")
(def shot "/")
(def new-line "\n")

(def bs-player-a (atom
                  {:board {},
                   :shot {},
                   :ships []}))
(def bs-player-b (atom
                  {:board {},
                   :shot {}},
                  :ships []))

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
  ([n] (bs-ship-get n ships))
  ([n ships] (first (filter #(= n (:name %)) ships))))

(bs-ship-get "taco") ;=> nil

(defn bs-ship-get-all
  ([n] (bs-ship-get-all n ships))
  ([n ships] (filter #(= n (:name %)) ships)))

(bs-ship-get-all "taco") ;=> ()

(defn bs-ship-coordinate-offset [orientation coordinates offset]
  (if (= "horizontal" orientation)
    [(+ (get coordinates 0) offset) (get coordinates 1)]
    [(get coordinates 0) (+ (get coordinates 1) offset)]))

(defn bs-ship-is-available [player ship]
  (or
   (nil? (bs-ship-get ship (:ships @player)))
   (try (< (:amount (bs-ship-get ship (:ships @player))) (:amount (bs-ship-get ship)))
     (catch Exception e false))
   ))

(defn bs-player-has-ship? [player ship]
  ((comp not nil?) (bs-ship-get ship (:ships @player))))

(bs-player-has-ship? bs-player-a "destroyer")

(defn bs-player-ship-at [player ship]
  (.indexOf (:ships @player) (bs-ship-get ship (:ships @player))))

(bs-ship-is-available bs-player-a "taco")

(defn bs-ship-put-is-legal? [player ship orientation coordinates]
  (if (and
       ((comp not nil?) (bs-ship-get ship))
       (bs-ship-is-available player ship))
    (loop [offset 0]
      (if (try (<= offset (:size (bs-ship-get ship))) ; refactor to use fnil?
            (catch Exception e false)
            )
        (if (= default (bs-cell-get (:board @player) (bs-ship-coordinate-offset orientation coordinates offset)))
          (recur (inc offset))
          false)
        true))
    false))

(bs-ship-put-is-legal? bs-player-a "aircraft carrier" "horizontal" [0 0]) ;=> true
(bs-ship-put-is-legal? bs-player-a "taco" "horizontal" [0 0]) ;=> false

(let [ship "aircraft carrier"
      player bs-player-a]
  (#(get-in % [:ships]) @player)
  )

;(swap! bs-player-a update-in [:ships] #(into % [(bs-ship-get "aircraft carrier")]))


(def bs-ship-put
  (fn [player ship orientation coordinates] ; coordinates are top or left, respectively
    (if (bs-ship-put-is-legal? player ship orientation coordinates)
      (do
        (loop [offset 0]
          (if-not (>= offset (:size (bs-ship-get ship)))
            (do
              (swap! player #(assoc-in % [:board (bs-ship-coordinate-offset orientation coordinates offset)]
                                       boat))
              (recur (inc offset)))
            ))
        (let [ship-amount (if (bs-player-has-ship? player ship)
                              (inc (:amount (bs-ship-get ship (:ships @player))))
                              1),
              ship-to-add (assoc-in (bs-ship-get ship) [:amount] ship-amount)]
        (do
          (if (bs-player-has-ship? player ship)
            (swap! player update-in [:ships (bs-player-ship-at player ship)] (fn [x] ship-to-add))
            (swap! player update-in [:ships] #(into % [ship-to-add]))
            )
          ))
        true)
      false)))

(:board @bs-player-a)
(bs-ship-put bs-player-a "aircraft carrier" "horizontal" [0 0])
(:board @bs-player-a)
(bs-ship-put bs-player-a "aircraft carrier" "horizontal" [0 0])
(bs-ship-put bs-player-a "aircraft carrier" "horizontl"[0 0])
(bs-ship-put bs-player-a "destroyer" "horizontal" [0 1])
(:board @bs-player-a)
(:ships @bs-player-a)
(< (:amount (bs-ship-get "destroyer" (:ships @bs-player-a))) (:amount (bs-ship-get "destroyer")))
(bs-ship-is-available bs-player-a "destroyer")
(:amount (bs-ship-get "destroyer" (:ships @bs-player-a)))

(bs-player-ship-at bs-player-a "destroyer")
(bs-ship-get "destroyer")
(bs-ship-put bs-player-a "destroyer" "horizontal" [0 2])

(println (bs-board-string (:board @bs-player-a)))
(:ships @bs-player-a)


;; Making a move
(def bs-shoot
  (fn [coordinates player opponent]
    (swap! player
           #(assoc-in % [:shot coordinates]
                      (if (= boat (bs-cell-get (:board @opponent)))
                        hit
                        shot)))))

;; Randomised ship setup

