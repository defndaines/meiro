(ns meiro.hex
  "Sigma (hexagon) maze generation.
  Hexagonal grids connect north to south, northwest to southeast,
  and northeast to southwest. Otherwise, they maintain a rectangular grid,
  so can build upon a core `init`.
  Hexagons stagger across the row, and these algorithms assume [0 0] is
  northwest from [0 1]."
  (:require [meiro.core :as m]))


(defn neighbors
  "Get all potential neighbors of a position in a given grid."
  [grid pos]
  (let [[row col] pos]
    (filter
      #(m/in? grid %)
      (concat
        [[(dec row) col] [(inc row) col]]  ; north-south
        (if (even? col)
          [[(dec row) (dec col)] [(dec row) (inc col)]
           [row (dec col)] [row (inc col)]]
          [[row (dec col)] [row (inc col)]
           [(inc row) (dec col)] [(inc row) (inc col)]]
          )))))


(defn direction
  "Get the direction from pos-1 to pos-2."
  [[row-1 col-1] [row-2 col-2]]
  (cond
    (and (= col-1 col-2) (< row-1 row-2)) :south
    (and (= col-1 col-2) (> row-1 row-2)) :north
    (and (= row-1 row-2) (odd? col-1) (< col-1 col-2)) :northeast
    (and (= row-1 row-2) (odd? col-1) (> col-1 col-2)) :northwest
    (and (= row-1 row-2) (even? col-1) (< col-1 col-2)) :southeast
    (and (= row-1 row-2) (even? col-1) (> col-1 col-2)) :southwest
    (and (< row-1 row-2) (< col-1 col-2)) :southeast
    (and (< row-1 row-2) (> col-1 col-2)) :southwest
    (and (> row-1 row-2) (< col-1 col-2)) :northeast
    (and (> row-1 row-2) (> col-1 col-2)) :northwest))


(defn link
  "Link two adjacent cells in a maze."
  ;; TODO Difference from core is no adjacency check.
  [maze pos-1 pos-2]
  (-> maze
      (update-in pos-1 conj (direction pos-1 pos-2))
      (update-in pos-2 conj (direction pos-2 pos-1))))
