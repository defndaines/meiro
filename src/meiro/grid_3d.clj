(ns meiro.grid-3d
  "Utilities for generating three dimensional mazes.
  In addition to the four cardinal directions, cells can link up and down.
  Positions are identified by [level, row, column].")


;;; Position Functions

(defn adjacent?
  "Are two positions adjacent.
  Function does not check that positions are within the bounds of a grid."
  [[level-1 row-1 col-1] [level-2 row-2 col-2]]
  (or
    (and (= level-1 level-2)
         (= row-1 row-2)
         (= 1 (Math/abs ^int (- col-1 col-2))))
    (and (= level-1 level-2)
         (= 1 (Math/abs ^int (- row-1 row-2)))
         (= col-1 col-2))
    (and (= 1 (Math/abs ^int (- level-1 level-2)))
         (= row-1 row-2)
         (= col-1 col-2))))


(defn direction
  "Get the direction from pos-1 to pos-2.
  Assumes [0 0 0] is the upper-north-west corner."
  [[level-1 row-1 col-1] [level-2 row-2 col-2]]
  (case [(- level-1 level-2) (- row-1 row-2) (- col-1 col-2)]
    [1 0 0] :up
    [-1 0 0] :down
    [0 1 0] :north
    [0 -1 0] :south
    [0 0 1] :west
    [0 0 -1] :east
    nil))


(defn init
  "Initialize a grid of cells with the given number of levels, rows, and
  columns, which can be accessed by index. Conceptually, [0 0 0] is the upper
  north-west corner."
  ([levels rows columns] (init levels rows columns []))
  ([levels rows columns v]
   (vec (repeat levels
                (vec (repeat rows
                             (vec (repeat columns v))))))))


(defn in?
  "Is the position within the bounds of the grid."
  [grid [level row col]]
  (let [max-level (dec (count grid))
        max-row (dec (count (get grid level)))
        max-col (dec (count (get-in grid [level row])))]
    (and
      (<= 0 level max-level)
      (<= 0 row max-row)
      (<= 0 col max-col))))


(defn neighbors
  "Get all potential neighbors of a position in a given grid"
  [grid [level row col]]
  (filter
    #(in? grid %)
    #{[(dec level) row col] [(inc level) row col]
      [level (dec row) col] [level (inc row) col]
      [level row (dec col)] [level row (inc col)]}))


(defn random-pos
  "Select a random position from the grid."
  [grid]
  (let [pos [(rand-int (count grid))
             (rand-int (count (first grid)))
             (rand-int (count (ffirst grid)))]]
    ;; When grids contain masked cells, make sure to return an unmasked cell.
    (if (empty? (get-in grid pos))
      pos
      (random-pos grid))))
