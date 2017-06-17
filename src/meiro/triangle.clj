(ns meiro.triangle
  "Triangle maze generation.
  Triangle mazes rely on a normal grid, but assume the [0 0] cell is positioned
  pointing up, with the possibility of connecting to a cell to the south.
  Each triangle then alternates orientation, such that a triangle pointing down
  can only link to cells to the north, east, and west."
  (:require [meiro.core :as m]))


(defn neighbors
  "Get all potential neighbors of a position in a given grid."
  [grid [row col]]
  (filter
    #(m/in? grid %)
    [[row (dec col)] [row (inc col)]
     (if (even? (+ row col))
       [(inc row) col]
       [(dec row) col])]))
