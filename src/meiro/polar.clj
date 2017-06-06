(ns meiro.polar
  "Polar (circular) maze generation.
  Although a rectangular grid can be used to generate polar mazes, this leads to
  mazes pinched in the center and wide at the edges. The solution presented here
  seeks to maintain roughly square cell proportions by increasing the number of
  cells per row as it gets further away from the center. Polar grids cannot
  simply rely on simple coordinate math to determine neighbors. All polar cells,
  except the center, will have a single 'north' (inward), 'east' (clockwise),
  and 'west' (counter-clockwise) neighbor, but can have multiple 'south'
  (outward) neighbors."
  (:require [meiro.core :as m])
  (:import (java.lang Math)))


(defn init
  "Initialize a polar grid of cells with the given number of rows,
  which can be accessed by index. Conceptually, [0 0] is the center;
  [1, 0] is the cell directly 'east' from the center. Rendering fuctions expect
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
           (recur (conj acc (repeat cells v)) (inc row)))
         acc)))))
