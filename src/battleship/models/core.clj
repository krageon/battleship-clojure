;; Battleship in clojure core
(ns battleship.models.core)

(def coord-display-to-backend
  (let [xcharoffset (int \A)]
    (fn [x]
      (let [letter (first x),
            number ((comp str first rest) x)]
        [(- (int letter) xcharoffset) (- (Integer/parseInt number) 1)]))))

;; Printing the board

; constants
(def default "~")
(def boat "o")
(def hit "x")
(def shot "/")
(def new-line "\n")

(def bs-cell-get
  (fn [board coordinates]
    (let [at-coordinates (board coordinates)]
      (if (nil? at-coordinates)
        default
        at-coordinates))))

(def bs-board-line
  (fn [board maxX y]
    (loop [x 0
           result []]
      (if (<= maxX x)
        (do
          (println x result)
          (recur (inc x) (assoc result (count result) (bs-cell-get board [x y]))))
        result))))

(bs-board-line {} 10 0)

; converting a board (a sparse array-thing) to a string, filling in the gaps
(def bs-board
  (fn [board]
    (loop [maxX 10
           y 0
           maxY 10
           result []]
      (if (> y maxY)
        result
        (recur maxX (inc y) maxY (assoc result (count result) (bs-board-line board maxX y)))))))

(bs-board {})

;; Picking ship locations
(def ships [{:name "Aircraft Carrier"
             :amount "1"
             :size "5"}
            {:name "Battleship"
             :amount "1"
             :size "4"}
            {:name "Cruiser"
             :amount "1"
             :size "3"}
            {:name "Destroyer"
             :amount "2"
             :size "2"}
            {:name "Submarine"
             :amount "2"
             :size "1"}
            ])

(defn bs-ship-get
  ([n] (bs-ship-get n ships))
  ([n ships] (first (filter #(= n (:name %)) ships))))

;(bs-ship-get "taco") ;=> nil

(defn bs-ship-get-all
  ([n] (bs-ship-get-all n ships))
  ([n ships] (filter #(= n (:name %)) ships)))

;(bs-ship-get-all "taco") ;=> ()

(defn bs-ship-coordinate-offset [orientation coordinates offset]
  (if (= "horizontal" orientation)
    [(+ (get coordinates 0) offset) (get coordinates 1)]
    [(get coordinates 0) (+ (get coordinates 1) offset)]))

(defn bs-ship-is-available [player ship]
  (or
   (nil? (bs-ship-get ship (:ships player)))
   (try (< (:amount (bs-ship-get ship (:ships player))) (:amount (bs-ship-get ship)))
     (catch Exception e false))
   ))

(defn bs-player-has-ship? [player ship]
  ((comp not nil?) (bs-ship-get ship (:ships player))))

(defn bs-player-ship-at [player ship]
  (.indexOf (:ships player) (bs-ship-get ship (:ships player))))

(defn bs-ship-put-is-legal? [player ship orientation coordinates]
  (if (and
       ((comp not nil?) (bs-ship-get ship))
       (bs-ship-is-available player ship))
    (loop [offset 0]
      (if (try (<= offset (:size (bs-ship-get ship))) ; refactor to use fnil?
            (catch Exception e false)
            )
        (if (= default (bs-cell-get (:board player) (bs-ship-coordinate-offset orientation coordinates offset)))
          (recur (inc offset))
          false)
        true))
    false))

(def bs-ship-draw
  (fn [player ship orientation coordinates]
    (loop [offset 0
           p player]
      (if-not (>= offset (:size (bs-ship-get ship)))
        (recur (inc offset) (assoc-in p [:board (bs-ship-coordinate-offset orientation coordinates offset)] boat))
        p))))

(def bs-ship-put
  (fn [player ship orientation coordinates] ; coordinates are top or left, respectively
    (if (bs-ship-put-is-legal? player ship orientation coordinates)
      (let [ship-amount (if (bs-player-has-ship? player ship)
                          (inc (:amount (bs-ship-get ship (:ships player))))
                          1),
            ship-to-add (assoc-in (bs-ship-get ship) [:amount] ship-amount)
            p (bs-ship-draw player ship orientation coordinates)]
        (if (bs-player-has-ship? p ship)
          (update-in p [:ships (bs-player-ship-at p ship)] (fn [x] ship-to-add))
          (update-in p [:ships] #(into % [ship-to-add]))))
      player)))

;; Making a move
(def bs-shoot
  (fn [coordinates player opponent]
    (assoc-in player [:shot coordinates]
              (if (= boat (bs-cell-get (:board opponent)))
                hit
                shot))))


(defn have-won? [player other]
  false)

;; Randomised ship setup
(defn ai-move [me them]
  me)