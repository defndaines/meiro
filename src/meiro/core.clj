(ns meiro.core)

;; Maze is vector of vectors. Access by [row column].
;; Data in cell is which directions are open to the next cell.

(defn init-maze
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

(defn adjacent?
  "Are two cells adjacent.
  Cells not within the bounds of a maze are considered not adjacent."
  [maze cell-1 cell-2]
  (let [max-row (dec (count maze))
        max-col (dec (count (first maze)))
        [row-1 col-1] cell-1
        [row-2 col-2] cell-2]
    (and
      (<= 0 row-1 max-row)
      (<= 0 row-2 max-row)
      (<= 0 col-1 max-col)
      (<= 0 col-2 max-col)
      (or
        (and (= row-1 row-2) (= 1 (Math/abs (- col-1 col-2))))
        (and (= col-1 col-2) (= 1 (Math/abs (- row-1 row-2))))))))

(defn link
  "Link two adjacent cells in a maze."
  [maze cell-1 cell-2]
  (if (adjacent? maze cell-1 cell-2)
    (-> maze
        (update-in cell-1 conj (direction cell-1 cell-2))
        (update-in cell-2 conj (direction cell-2 cell-1)))
    maze))

;; If need to unlink? ... (remove #{[1 2]} coll)
