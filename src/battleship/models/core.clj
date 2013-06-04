;; Battleship in clojure core

;; use these as an interface:
; bs-ship-put: (fn [player ship orientation coordinates]) ; coordinates are top or left, respectively
; (bs-board-string (:board player-data)) ; player a or b as required, :board and :shot as required
; (coord-display-to-backend "A2") ;=> [0 1]

(def examplecoord "J2")
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

; Move this to the noir session stuff
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
   (nil? (bs-ship-get ship (:ships player)))
   (try (< (:amount (bs-ship-get ship (:ships player))) (:amount (bs-ship-get ship)))
     (catch Exception e false))
   ))

(defn bs-player-has-ship? [player ship]
  ((comp not nil?) (bs-ship-get ship (:ships player))))

(defn bs-player-ship-at [player ship]
  (.indexOf (:ships player) (bs-ship-get ship (:ships player))))

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

(def bs-ship-put
  (fn [player ship orientation coordinates] ; coordinates are top or left, respectively
    (if (bs-ship-put-is-legal? player ship orientation coordinates)
      (do
        (loop [offset 0]
          (if-not (>= offset (:size (bs-ship-get ship)))
            (do
              (assoc-in player :board (bs-ship-coordinate-offset orientation coordinates offset)] boat)
              (recur (inc offset)))
            ))
        (let [ship-amount (if (bs-player-has-ship? player ship)
                              (inc (:amount (bs-ship-get ship (:ships @player))))
                              1),
              ship-to-add (assoc-in (bs-ship-get ship) [:amount] ship-amount)]
        (do
          (if (bs-player-has-ship? player ship)
            (update-in player [:ships (bs-player-ship-at player ship)] (fn [x] ship-to-add))
            (update-in player [:ships] #(into % [ship-to-add]))
            )
          ))
        player)
      nil)))

;; Making a move
(def bs-shoot
  (fn [coordinates player opponent]
    (assoc-in player [:shot coordinates]
              (if (= boat (bs-cell-get (:board opponent)))
                hit
                shot))))

;; Randomised ship setup
