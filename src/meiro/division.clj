(ns meiro.division
  "The Recursive Division algorithm is a 'wall adding' algorithm, which divides
  a completely open grid into subsections until it can no longer be subdivided."
  (:require [meiro.core :as m]))


(defn- link-all
  "Generate a grid where all cells are already linked."
  [grid]
  (let [rows (count grid)
        columns (count (first grid))]
    (as-> grid g
      (reduce (fn [acc e] (update-in acc e conj :north)) g
              (for [row (range 1 rows) col (range columns)] [row col]))
      (reduce (fn [acc e] (update-in acc e conj :south)) g
              (for [row (range (dec rows)) col (range columns)] [row col]))
      (reduce (fn [acc e] (update-in acc e conj :east)) g
              (for [row (range rows) col (range (dec columns))] [row col]))
      (reduce (fn [acc e] (update-in acc e conj :west)) g
              (for [row (range rows) col (range 1 columns)] [row col])))))


(defn- divide-horizontal
  "Divide a grid horizontally."
  [grid row col height width]
  (let [south-of (rand-int (dec height))
        passage-at (rand-int width)]
    (reduce
      (fn [acc e]
        (m/unlink acc e (m/south e)))
      grid
      (for [x (range width) :when (not= x passage-at)] [(+ row south-of) x]))))


(defn- divide-vertical
  "Divide a grid vertically."
  [grid row col height width]
  (let [east-of (rand-int (dec width))
        passage-at (rand-int height)]
    (reduce
      (fn [acc e]
        (m/unlink acc e (m/east e)))
      grid
      (for [y (range height) :when (not= y passage-at)] [y (+ col east-of)]))))


(defn- divide
  "Divide a grid."
  [grid row column height width]
  (when (and (< 1 width) (< 1 height))
    (if (> height width)
      (divide-horizontal grid row column height width)
      (divide-vertical grid row column height width))))


(defn create
  "Create a random maze using the Recursive Division algorithm."
  [grid]
  (link-all grid))
