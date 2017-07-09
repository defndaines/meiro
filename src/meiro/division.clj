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
  [grid row col height width div-fn]
  (let [south-of (rand-int (dec height))
        passage-at (+ col (rand-int width))]
    (->
      (reduce
        (fn [acc e] (m/unlink acc e (m/south e)))
        grid
        (for [c (range col (+ col width))
              :when (not= c passage-at)]
          [(+ row south-of) c]))
      (divide row col (inc south-of) width div-fn)
      (divide (+ row south-of 1) col (- height south-of 1) width div-fn))))


(defn- divide-vertical
  "Divide a grid vertically."
  [grid row col height width div-fn]
  (let [east-of (rand-int (dec width))
        passage-at (+ row (rand-int height))]
    (->
      (reduce
        (fn [acc e] (m/unlink acc e (m/east e)))
        grid
        (for [r (range row (+ row height))
              :when (not= r passage-at)]
          [r (+ col east-of)]))
      (divide row col height (inc east-of) div-fn)
      (divide row (+ col east-of 1) height (- width east-of 1) div-fn))))


(defn- divide?
  "Should a grid with the given height and width be divided further?"
  [height width]
  (or (<= height 1) (<= width 1)))


(defn- divide-fn
  "Generate a function to determine whether a grid with the given height and
  width be divided further?
  Allow for random rooms of up to `size` to be created within the maze.
  Rooms will be created at `rate` frequency whenever a sub-grid is below the
  `size` threshold, such that 1.0 will always create rooms and 0.0 will never."
  [size rate]
  (fn [height width]
    (or (<= height 1) (<= width 1)
        (and (<= height size) (<= width size) (< (rand) rate)))))


(defn- divide
  "Divide a grid.
  The function will work against a sub-grid defined by the `row` and `column`
  as the northwest corner and extending `height` and `width` from that point.
  A wall with a single passage will be created within the sub-grid, and then
  each recursively call this function with the resulting sub-grid from that
  division."
  ([grid row column height width] (divide grid row column height width divide?))
  ([grid row column height width decision-fn]
   (if (decision-fn height width)
     grid
     (if (> height width)
       (divide-horizontal grid row column height width decision-fn)
       (divide-vertical grid row column height width decision-fn)))))


(defn create
  "Create a random maze using the Recursive Division algorithm."
  ([grid]
   (divide (link-all grid) 0 0 (count grid) (count (first grid))))
  ([grid room-size room-rate]
   (divide (link-all grid) 0 0 (count grid) (count (first grid))
           (divide-fn room-size room-rate))))
