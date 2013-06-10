;; Battleship in clojure core
(ns battleship.models.core)

(def coord-display-to-backend
  (let [xcharoffset (int \A)]
    (fn [x]
      (let [letter (first x),
            number ((comp first rest) x)]
        [(-
          (if (or (= (type number) java.lang.Long)
                  (= (type number) java.lang.Integer))
            number
            (Integer/parseInt (str number)))
          1)
         (- (int letter) xcharoffset)]))))

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
      (if (< x maxX)
        (recur (inc x) (assoc result (count result) (bs-cell-get board [x y])))
        result))))

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

;(bs-board {:board {} :shot {} :ships []})

;; Picking ship locations
(def ships [{:name "Aircraft Carrier"
             :amount 1
             :size 5}
            {:name "Battleship"
             :amount 1
             :size 4}
            {:name "Cruiser"
             :amount 1
             :size 3}
            {:name "Destroyer"
             :amount 2
             :size 2}
            {:name "Submarine"
             :amount 2
             :size 1}
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
    (if orientation
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
    [(assoc-in player [:shot coordinates]
               (if (= boat (bs-cell-get (:board opponent)))
                 hit
                 shot))
     (assoc-in opponent [:board coordinates]
               (if (= boat (bs-cell-get (:board opponent)))
                 hit
                 shot))]))

(defn have-won? [player other]
  (every? (fn [x] (every? #(not= "o" %) x)) (bs-board other)))

;; Randomised ship setup
; very fucking expensive, but *so* random :D
(defn ai-setup [me]
  (loop [current 0
         player me]
    (if (< current (count ships))
      (let [ship (get ships current)
            coordinates [(rand-int 10) (rand-int 10)]
            orientation (= 0 (rand-int 1))
            player-proposed (bs-ship-put player (:name ship) orientation coordinates)]
        (if (not= player player-proposed)
          (recur (inc current) player-proposed)
          (recur current player)))
      player)))

;  CPU has a 5% hit chance ATM - this is a stupid fkn shot algorithm.
; TODO: Refactor into a diagonal hitscan with subsequent gap-fill to find the submarines - this simulates a moderately skilled player
;         and isn't cheaty like this
(defn ai-shoot [me them]
  (if (> 5 (rand-int 100))
    (let [board (:board them)
          hit ((first (filter #(= "o" (% 1)) (seq board))) 0)]
      (bs-shoot hit me them))
    [me them]))