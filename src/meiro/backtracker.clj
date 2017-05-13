(ns meiro.backtracker
  "The Recursive Backtracker algorithm uses a biased random walk to generate a
  maze. While there are unvisited cells, it chooses one at random, but when it
  encounters a dead end, it backtracks on its path until it finds a cell with
  unvisited neighbors and walks, repeating until all cells have been linked."
  (:require [meiro.core :as m]))

(defn create
  "Create a random maze using the Recursive Backtracker algorithm."
  [grid]
  (loop [maze grid
         pos (m/random-pos grid)
         stack '(pos)]
    (if (seq stack)
      (let [unvisited (m/empty-neighbors maze pos)]
        (if (seq unvisited)
          (let [neighbor (rand-nth unvisited)]
            (recur (m/link maze pos neighbor) neighbor (conj stack neighbor)))
          (recur maze (first stack) (rest stack))))
      maze)))
