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


(defn- weave-cell-fn
  "Generate a function which which check for a weave candidate in a given
  direction."
  [corridor-fn dir-fn next-fn]
  (fn
    [maze pos]
    (when (corridor-fn (get-in maze (next-fn pos)))
      (first
        (take-while
          (fn [pos]
            (empty? (get-in maze pos)))
          (drop-while
            (fn [pos]
              (corridor-fn (get-in maze pos)))
            (cells-to maze dir-fn (next-fn pos))))))))


(def cell-west
  "Return a cell to the west if it matches weave conditions."
  (weave-cell-fn north-south? m/west
                 (fn [[row col]] [row (dec col)])))


(def cell-east
  "Return a cell to the east if it matches weave conditions."
  (weave-cell-fn north-south? m/east
                 (fn [[row col]] [row (inc col)])))


(def cell-north
  "Return a cell to the north if it matches weave conditions."
  (weave-cell-fn east-west? m/north
                 (fn [[row col]] [(dec row) col])))


(def cell-south
  "Return a cell to the south if it matches weave conditions."
  (weave-cell-fn east-west? m/south
                 (fn [[row col]] [(inc row) col])))


(defn neighbors
  "Get all potential neighbors of a position in a given maze."
  [maze pos]
  (filter
    #(m/in? maze %)
    (list
      (cell-north maze pos) (m/north pos)
      (m/south pos) (cell-south maze pos)
      (m/east pos) (cell-east maze pos)
      (cell-west maze pos) (m/west pos))))
