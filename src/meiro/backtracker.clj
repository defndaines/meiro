(ns meiro.backtracker
  "The Recursive Backtracker algorithm uses a biased random walk to generate a
  maze. While there are unvisited cells, it chooses one at random, but when it
  encounters a dead end, it backtracks on its path until it finds a cell with
  unvisited neighbors and walks, repeating until all cells have been linked."
  (:require [meiro.core :as m]))

(defn create
  "Create a random maze using the Recursive Backtracker algorithm.
  If a `pos` is passed, then the random walk will begin at that position.
  `neighbor-fn` and `direction-fn` allow for alternative (e.g., polar) mazes
  to use the algorithm."
  ([grid] (create grid (m/random-pos grid)))
  ([grid pos] (create grid pos m/neighbors m/direction))
  ([grid pos neighbor-fn direction-fn]
   (loop [maze grid
          pos pos
          stack (list pos)]
     (if (seq stack)
       (let [unvisited (m/empty-neighbors maze neighbor-fn pos)]
         (if (seq unvisited)
           (let [neighbor (rand-nth unvisited)]
             (recur
               (m/link maze direction-fn pos neighbor)
               neighbor
               (conj stack neighbor)))
           (recur
             maze
             (second stack)
             (rest stack))))
       maze))))
