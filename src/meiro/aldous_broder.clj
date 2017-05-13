(ns meiro.aldous-broder
  "Aldous-Broder uses a random walk to generate an unbiased maze.
  Because it randomly navigates to the next cell without regard to
  cells it has already visited, it can take an excessively long time to
  generate, especially on large mazes."
  (:require [meiro.core :as m]))

(defn create
  "Create a random maze using the Aldous-Broder algorithm."
  [maze]
  (loop [acc maze
         cell (m/random-pos maze)
         unvisited (dec (* (count maze) (count (first maze))))]
    (if (zero? unvisited)
      acc
      (let [neighbor (rand-nth (m/neighbors acc cell))]
        (if (empty? (get-in acc neighbor))
          (recur (m/link acc cell neighbor) neighbor (dec unvisited))
          (recur acc neighbor unvisited))))))
