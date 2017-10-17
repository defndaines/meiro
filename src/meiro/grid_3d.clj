(ns meiro.grid-3d
  "Utilities for generating three dimensional mazes.
  In addition to the four cardinal directions, cells can link up and down.
  Positions are identified by [level, row, column]."
  (:require [clojure.spec.alpha :as spec]))


(spec/def ::pos (spec/tuple :level nat-int? :row nat-int? :col nat-int?))

(spec/def ::direction #{:up :down :north :south :east :west})

(spec/def ::grid
  (spec/coll-of
    (spec/coll-of
      (spec/coll-of (spec/and coll? empty?) :kind vector? :into vector?)
      :kind vector? :into vector?)
    :kind vector? :into vector?))


;;; Position Functions

(spec/fdef adjacent?
  :args (spec/cat :pos-0 ::pos :pos-1 ::pos)
  :ret boolean?)
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


(spec/fdef direction
  :args (spec/cat :pos-0 ::pos :pos-1 ::pos)
  :ret ::direction)
(defn direction
  "Get the direction from pos-1 to pos-2.
  Assumes [0 0 0] is the lower-north-west corner."
  [[level-1 row-1 col-1] [level-2 row-2 col-2]]
  (case [(- level-1 level-2) (- row-1 row-2) (- col-1 col-2)]
    [1 0 0] :down
    [-1 0 0] :up
    [0 1 0] :north
    [0 -1 0] :south
    [0 0 1] :west
    [0 0 -1] :east
    nil))


(spec/fdef init
  :args (spec/alt
          :3-args (spec/cat :level nat-int? :row nat-int? :col nat-int?)
          :4-args (spec/cat :level nat-int? :row nat-int? :col nat-int?
                            :v (spec/and coll? empty?)))
  :ret ::grid)
(defn init
  "Initialize a grid of cells with the given number of levels, rows, and
  columns, which can be accessed by index. Conceptually, [0 0 0] is the lower
  north-west corner."
  ([levels rows columns] (init levels rows columns []))
  ([levels rows columns v]
   (vec (repeat levels
                (vec (repeat rows
                             (vec (repeat columns v))))))))


(spec/fdef in?
  :args (spec/cat :grid ::grid :pos ::pos)
  :ret boolean?)
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


(spec/fdef neighbors
  :args (spec/cat :grid ::grid :pos ::pos)
  :ret (spec/coll-of ::pos))
(defn neighbors
  "Get all potential neighbors of a position in a given grid"
  [grid [level row col]]
  (filter
    #(in? grid %)
    #{[(dec level) row col] [(inc level) row col]
      [level (dec row) col] [level (inc row) col]
      [level row (dec col)] [level row (inc col)]}))


(spec/fdef random-pos
  :args (spec/cat :grid ::grid)
  :ret ::pos)
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
