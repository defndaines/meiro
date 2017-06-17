(ns meiro.polar
  "Polar (circular) maze generation.
  Although a rectangular grid can be used to generate polar mazes, this leads to
  mazes pinched in the center and wide at the edges. The solution presented here
  seeks to maintain roughly square cell proportions by increasing the number of
  cells per row as it gets further away from the center. Polar grids cannot
  rely on simple coordinate math to determine neighbors. All polar cells,
  except the center, will have a single 'inward', 'clockwise',
  and 'counter-clockwise' neighbor, but can have multiple 'outward'
  neighbors."
  (:require [meiro.core :as m])
  (:import (java.lang Math)))


(defn init
  "Initialize a polar grid of cells with the given number of rows,
  which can be accessed by index. Conceptually, [0 0] is the center;
  [1, 0] is the cell directly 'east' from the center. Rendering functions expect
  that rows start along the positive x axis and rotate clockwise."
  ([rows] (init rows []))
  ([rows v]
   (let [height (/ 1.0 rows)]
     (loop [acc [[v]]
            row 1]
       (if (< row rows)
         (let [radius (/ row rows)
               circumference (* 2 Math/PI radius)
               prev (count (last acc))
               estimated-cell-width (/ circumference prev)
               ratio (Math/round (/ estimated-cell-width height))
               cells (* prev ratio)]
           (recur (conj acc (vec (repeat cells v))) (inc row)))
         acc)))))


(defn neighbors
  "Get all potential neighbors of a position in a given grid."
  [grid pos]
  (let [[row col] pos
        inward (count (get grid (dec row)))
        cells (count (get grid row))
        outward (count (get grid (inc row)))] ; 0 when row is last.
    (filter
      #(m/in? grid %)
      (concat
        ;; Inward
        (if (= 1 row) ; All row 1 cells have same parent.
          [[0 0]]
          [[(dec row) (int (Math/floor (* col (/ inward cells))))]])
        ;; Counter/Clockwise. Last cell is neighbor to first cell in row.
        (when (pos? row)
          [[row (mod (dec col) cells)] [row (mod (inc col) cells)]])
        ;; Outward
        (cond
          (zero? row) [[1 0] [1 1] [1 2] [1 3] [1 4] [1 5]]
          (zero? outward) []
          (= cells outward) [[(inc row) col]]
          :else [[(inc row) (* 2 col)] [(inc row) (inc (* 2 col))]])))))


(defn direction
  "Get the direction from pos-1 to pos-2.
  Assumes [0 0] is the center."
  [[row-1 col-1] [row-2 col-2]]
  (cond
    (< row-1 row-2) [row-2 col-2]  ; outward (just link cell)
    (> row-1 row-2) :inward
    (= col-1 (inc col-2)) :counter-clockwise
    (= col-2 (inc col-1)) :clockwise
    (zero? col-1) :counter-clockwise  ; wrap around
    (zero? col-2) :clockwise))  ; wrap around
