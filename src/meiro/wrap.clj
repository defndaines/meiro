(ns meiro.wrap
  "Functions for generating mazes which wrap around on themselves
  (think Pac-Man)."
  (:require [meiro.core :as m])
  (:import (java.lang Math)))


(defn neighbors
  "Get all potential neighbors of a position in a given grid.
  When a position is at the edge of the grid, wrap around to the other side."
  [grid [row col]]
  (let [height (count grid)
        width (count (first grid))]
    #{[(mod (dec row) height) col]
      [(mod (inc row) height) col]
      [row (mod (dec col) width)]
      [row (mod (inc col) width)]}))


(defn neighbors-horizontal
  "Get all potential neighbors of a position in a given grid.
  When a position is at first or last columns of a grid, wrap around to the
  other side."
  [grid [row col]]
  (let [width (count (first grid))]
    (filter
      #(m/in? grid %)
      #{[(dec row) col]
        [(inc row) col]
        [row (mod (dec col) width)]
        [row (mod (inc col) width)]})))


(defn direction
  "Get the direction from pos-1 to pos-2.
  Positions which wrap around the edge of the grid still use a cardinal
  direction so that rendered mazes treat it like a warp open in the edgs."
  [[row-1 col-1] [row-2 col-2]]
  (let [row-diff (- row-1 row-2)
        col-diff (- col-1 col-2)]
    (cond
      (= 0 row-diff) (cond
                       (= 1 col-diff) :west
                       (= -1 col-diff) :east
                       (= 0 col-1) :west
                       (= 0 col-2) :east)
      (= 0 col-diff) (cond
                       (= 1 row-diff) :north
                       (= -1 row-diff) :south
                       (= 0 row-1) :north
                       (= 0 row-2) :south)
      :else nil)))


(def link
  "Link cells in a wrapped grid."
  (m/link-with direction))
