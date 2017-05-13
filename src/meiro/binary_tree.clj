(ns meiro.binary-tree
  "Binary-tree produces mazes with a bias that flows down and to the right.
  All mazes will have a single corridor on both the southern and eastern edges."
  (:require [meiro.core :as m]))

(defn- south-east [maze pos]
  (filter
    #(m/in? maze %)
    (let [[row col] pos]
      [[(inc row) col] [row (inc col)]])))

(defn- link-neighbor
  [maze pos]
  (let [neighbors (south-east maze pos)]
    (if (seq neighbors)
      (m/link maze pos (rand-nth neighbors))
      maze)))

(defn create
  "Create a random grid using the binary tree algorithm."
  [grid]
  (reduce
    link-neighbor
    grid
    (for [row (range (count grid)) col (range (count (first grid)))]
      [row col])))
