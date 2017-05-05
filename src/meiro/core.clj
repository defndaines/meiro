(ns meiro.core)

;; Maze is vector of vectors. Access by [row column].
;; Data in cell is which directions are open to the next cell.

(defn init
  "Initialize a maze. A grid of cells with the given number of rows and columns,
  which can be accessed by index, with [0 0] as the upper left corner."
  [rows columns]
  (vec (repeat rows
               (vec (repeat columns [])))))

(defn direction
  "Get the direction from cell-1 to cell-2. Assumes [0 0] is the north-west corner."
  [cell-1 cell-2]
  (let [[row-1 col-1] cell-1 [row-2 col-2] cell-2]
    (case [(- row-1 row-2) (- col-1 col-2)]
      [0 1] :west
      [0 -1] :east
      [1 0] :north
      [-1 0] :south
      nil)))

(defn north
  "Get cell to the north of a given cell. No bounds checking, so may return an invalid cell."
  [cell]
  (let [[row col] cell]
    [(dec row) col]))

(defn south
  "Get cell to the south of a given cell. No bounds checking, so may return an invalid cell."
  [cell]
  (let [[row col] cell]
    [(inc row) col]))

(defn east
  "Get cell to the east of a given cell. No bounds checking, so may return an invalid cell."
  [cell]
  (let [[row col] cell]
    [row (inc col)]))

(defn west
  "Get cell to the west of a given cell. No bounds checking, so may return an invalid cell."
  [cell]
  (let [[row col] cell]
    [row (dec col)]))

(defn cell-to
  "Get neighboring cell given a direction. No bounds checking, so may return invalid cell."
  [cardinal cell]
  (let [[row col] cell]
    (case cardinal
      :north [(dec row) col]
      :south [(inc row) col]
      :east [row (inc col)]
      :west [row (dec col)])))

(defn cells-west
  "Get a sequence of cells west of the cell, including the cell."
  [maze cell]
  (if (seq (filter #{:west} (get-in maze cell)))
    (cons cell (cells-west maze (west cell)))
    [cell]))

(defn in? [maze cell]
  (let [max-row (dec (count maze))
        max-col (dec (count (first maze)))
        [row col] cell]
    (and
      (<= 0 row max-row)
      (<= 0 col max-col))))

(defn adjacent?
  "Are two cells adjacent.
  Cells not within the bounds of a maze are considered not adjacent."
  [maze cell-1 cell-2]
  (let [[row-1 col-1] cell-1
        [row-2 col-2] cell-2]
    (and
      (in? maze cell-1)
      (in? maze cell-2)
      (or
        (and (= row-1 row-2) (= 1 (Math/abs (- col-1 col-2))))
        (and (= col-1 col-2) (= 1 (Math/abs (- row-1 row-2))))))))

(defn neighbors
  "Get all potential neighbors of a cell in a given maze."
  [maze cell]
  (let [[row col] cell]
    (filter
      #(in? maze %)
      #{[(dec row) col] [(inc row) col] [row (dec col)] [row (inc col)]})))

(defn link
  "Link two adjacent cells in a maze."
  [maze cell-1 cell-2]
  (if (adjacent? maze cell-1 cell-2)
    (-> maze
        (update-in cell-1 conj (direction cell-1 cell-2))
        (update-in cell-2 conj (direction cell-2 cell-1)))
    maze))

;; If need to unlink? ... (remove #{[1 2]} coll)
