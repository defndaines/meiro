(ns meiro.core
  "Core maze-generation utilities.
  Mazes are represented as a vector of vectors, which can be accessed by
  [row column]. In a fully generated maze, each cell will contain the directions
  to open neighbor cells, e.g., [:east :south].")

;; Difference between "grid" and "maze"?
;; A grid has no links, a maze has links between the cells.

;; Difference between a "cell" and "pos" (or position).
;; A cell has links to neighbors, such as [:east :north].
;; A position is the [row col] index of a cell.

;; TODO May want to break apart code into "grid" and "maze" namespaces.

(defn init
  "Initialize a grid of cells with the given number of rows and columns,
  which can be accessed by index. Conceptually, [0 0] is the upper left corner."
  ([rows columns] (init rows columns []))
  ([rows columns v]
   (vec (repeat rows
                (vec (repeat columns v))))))

(defn direction
  "Get the direction from pos-1 to pos-2.
  Assumes [0 0] is the north-west corner."
  [pos-1 pos-2]
  (let [[row-1 col-1] pos-1 [row-2 col-2] pos-2]
    (case [(- row-1 row-2) (- col-1 col-2)]
      [0 1] :west
      [0 -1] :east
      [1 0] :north
      [-1 0] :south
      nil)))

;; TODO Unused. Here for "completeness", but by never be used.
(defn north
  "Get position to the north of a given position.
  No bounds checking, so may return an invalid position."
  [pos]
  (let [[row col] pos]
    [(dec row) col]))

(defn south
  "Get position to the south of a given position.
  No bounds checking, so may return an invalid position."
  [pos]
  (let [[row col] pos]
    [(inc row) col]))

(defn east
  "Get position to the east of a given position.
  No bounds checking, so may return an invalid position."
  [pos]
  (let [[row col] pos]
    [row (inc col)]))

(defn west
  "Get position to the west of a given position.
  No bounds checking, so may return an invalid position."
  [pos]
  (let [[row col] pos]
    [row (dec col)]))

(defn pos-to
  "Get neighboring position given a direction.
  No bounds checking, so may return invalid position."
  [cardinal pos]
  (let [[row col] pos]
    (case cardinal
      :north [(dec row) col]
      :south [(inc row) col]
      :east [row (inc col)]
      :west [row (dec col)])))

(defn path-west
  "Get a path sequence of positions west of the provided position, including that position."
  [maze pos]
  (if (seq (filter #{:west} (get-in maze pos)))
    (cons pos (path-west maze (west pos)))
    [pos]))

(defn in?
  "Is the position within the bounds of the grid."
  [grid pos]
  (let [max-row (dec (count grid))
        max-col (dec (count (first grid)))
        [row col] pos]
    (and
      (<= 0 row max-row)
      (<= 0 col max-col))))

(defn adjacent?
  "Are two positions adjacent.
  Positions not within the bounds of a grid are considered not adjacent."
  [grid pos-1 pos-2]
  (let [[row-1 col-1] pos-1
        [row-2 col-2] pos-2]
    (and
      (in? grid pos-1)
      (in? grid pos-2)
      (or
        (and (= row-1 row-2) (= 1 (Math/abs (- col-1 col-2))))
        (and (= col-1 col-2) (= 1 (Math/abs (- row-1 row-2))))))))

(defn neighbors
  "Get all potential neighbors of a position in a given grid"
  [grid pos]
  (let [[row col] pos]
    (filter
      #(in? grid %)
      #{[(dec row) col] [(inc row) col] [row (dec col)] [row (inc col)]})))

(defn all-positions
  "Get a sequence of all the positions in a grid."
  [grid]
  ;; Maybe put into set, but then would have to figure out how to
  ;; randomly select from set.
  (for [row (range (count grid))
        col (range (count (first grid)))]
    [row col]))

(defn random-pos
  "Select a random position from the grid."
  [grid]
  [(rand-int (count grid)) (rand-int (count (first grid)))])

;; TODO This is different from the same named function defined in dijkstra.
;;      May rethink.
(defn empty-neighbors
  "Get all positions neighboring `pos` which have not been visited."
  [maze pos]
  (filter #(empty? (get-in maze %)) (neighbors maze pos)))

(defn link
  "Link two adjacent cells in a maze."
  [maze pos-1 pos-2]
  (if (adjacent? maze pos-1 pos-2)
    (-> maze
        (update-in pos-1 conj (direction pos-1 pos-2))
        (update-in pos-2 conj (direction pos-2 pos-1)))
    maze))

(defn dead-ends
  "Filter for the dead ends in a maze.
  Fewer dead ends contribute to 'river', more flowing and meandering in a maze."
  [maze]
  (mapcat
    (fn [row] (filter #(= 1 (count %)) row))
    maze))
