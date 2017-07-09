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


(declare divide)


(defn- divide-horizontal
  "Divide a grid horizontally."
  [grid row col height width]
  (let [south-of (rand-int (dec height))
        passage-at (+ col (rand-int width))]
    (->
      (reduce
        (fn [acc e]
          (m/unlink acc e (m/south e)))
        grid
        (for [x (range col (+ col width))
              :when (not= x passage-at)]
          [(+ row south-of) x]))
      (divide row col (inc south-of) width)
      (divide (+ row south-of 1) col (- height south-of 1) width))))


(defn- divide-vertical
  "Divide a grid vertically."
  [grid row col height width]
  (let [east-of (rand-int (dec width))
        passage-at (+ row (rand-int height))]
    (->
      (reduce
        (fn [acc e]
          (m/unlink acc e (m/east e)))
        grid
        (for [r (range row (+ row height))
              :when (not= r passage-at)]
          [r (+ col east-of)]))
      (divide row col height (inc east-of))
      (divide row (+ col east-of 1) height (- width east-of 1)))))


(defn- divide
  "Divide a grid."
  [grid row column height width]
  (if (or (<= height 1) (<= width 1))
    grid
    (if (> height width)
      (divide-horizontal grid row column height width)
      (divide-vertical grid row column height width))))


(defn create
  "Create a random maze using the Recursive Division algorithm."
  [grid]
  (divide (link-all grid) 0 0 (count grid) (count (first grid))))
