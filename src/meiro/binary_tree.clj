(ns meiro.binary-tree
  (:require [meiro.core :as m]))

(defn- south-east [maze cell]
  (filter
    #(m/in? maze %)
    (let [[row col] cell]
      [[(inc row) col] [row (inc col)]])))

(defn- link-neighbor
  [maze cell]
  (let [neighbors (south-east maze cell)]
    (if (seq neighbors)
      (m/link maze cell (rand-nth neighbors))
      maze)))

(defn create
  "Create a random maze using the binary tree algorithm."
  [maze]
  (reduce
    link-neighbor
    maze
    (for [row (range (count maze)) col (range (count (first maze)))]
      [row col])))
