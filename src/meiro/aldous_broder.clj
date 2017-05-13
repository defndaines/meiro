(ns meiro.aldous-broder
  "Aldous-Broder uses a random walk to generate an unbiased maze.
  Because it randomly navigates to the next cell without regard to
  cells it has already visited, it can take an excessively long time to
  generate, especially on large mazes."
  (:require [meiro.core :as m]))

(defn create
  "Create a random maze using the Aldous-Broder algorithm."
  [grid]
  (loop [maze grid
         cell (m/random-pos grid)
         unvisited (dec (* (count grid) (count (first grid))))]
    (if (zero? unvisited)
      maze
      (let [neighbor (rand-nth (m/neighbors maze cell))]
        (if (empty? (get-in maze neighbor))
          (recur (m/link maze cell neighbor) neighbor (dec unvisited))
          (recur maze neighbor unvisited))))))
