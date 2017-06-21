(ns meiro.weave
  "Functions for generating a weave maze.
  Unless otherwise stated, these functions are designed to work on rectangular
  grids. Weave mazes are allowed to pass under neighbors when certain conditions
  are met.
  * Passages cannot dead end while underneath another cell.
  * Passages must be perpendicular, one north-south, one east-west.
  * Passages cannot change direction while traveling under other passages."
  (:require [meiro.core :as m]))


(defn- north-south?
  "Check if a cell is a north-south corridor."
  [cell]
  (= #{:north :south} (into #{} cell)))


(defn- east-west?
  "Check if a cell is an east-west corridor."
  [cell]
  (= #{:east :west} (into #{} cell)))


(defn- cells-to
  "Get a sequence of cell positions in a given direction."
  [maze dir-fn pos]
  (take-while
    #(m/in? maze %)
    (cons (dir-fn pos)
          (lazy-seq (cells-to maze dir-fn (dir-fn pos))))))


(defn cell-west
  "Return a cell to the west if it matches under-path conditions."
  [maze [row col]]
  (when (north-south? (get-in maze [row (dec col)]))
    (first
      (take-while
        (fn [pos]
          (empty? (get-in maze pos)))
        (drop-while
          (fn [pos]
            (north-south? (get-in maze pos)))
          (cells-to maze m/west [row (dec col)]))))))


(defn neighbors
  "Get all potential neighbors of a position in a given maze."
  [maze [row col]]
  (filter
    #(m/in? maze %)
    #{[(dec row) col] [(inc row) col] [row (dec col)] [row (inc col)]}))
