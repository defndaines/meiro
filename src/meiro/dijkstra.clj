;;; Dijkstra's algorithm for measuring distances.
(ns meiro.dijkstra
  (:require [meiro.core :as m]))

(defn- empty-neighbors
  "Given a grid of distances, find neighbors of a cell which haven't been calculated yet."
  [grid cell neighbors]
  (filter
    #(nil? (get-in grid %))
    (map #(m/cell-to % cell) neighbors)))

(defn distances
  "Calculate distances to each cell relative from starting cell.
  Assumes a perfect maze."
  ([maze] (distances maze [0 0]))
  ([maze cell] (distances maze cell (m/init (count maze) (count (first maze)) nil) 0))
  ([maze cell acc dist]
   (if (seq cell)
     (reduce
       #(distances maze %2 %1 (inc dist))
       (assoc-in acc cell dist)
       (empty-neighbors acc cell (get-in maze cell)))
     acc)))

(defn solution
  "Provide the path between to two cells in the maze."
  [maze start end]
  (let [dist (distances maze start)
        step (fn [cell n] (last (filter #(= (dec n) (get-in dist %)) (m/neighbors maze cell))))]
    (loop [acc '()
           cell end]
      (let [n (get-in dist cell)]
        (if (zero? n)
          (conj acc cell)
          (recur (conj acc cell) (step cell n)))))))
