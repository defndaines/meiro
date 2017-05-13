(ns meiro.hunt-and-kill
  "Hunt and Kill performs a random walk against unvisited cells, then searches
  for the first unvisited cell adjacent to a visited cell to link and continue,
  until all cells have been visited."
  (:require [meiro.core :as m]))

;; TODO This is different from the same named function elsewhere. May rethink.
(defn- empty-neighbors
  [maze cell]
  (filter #(empty? (get-in maze %)) (m/neighbors maze cell)))

(defn- visited-neighbors
  [maze cell]
  (remove #(empty? (get-in maze %)) (m/neighbors maze cell)))

(defn- hunt
  "Find first unvisited cell with a visited neighbor."
  [maze cells]
  (first
    (for [cell cells
          neighbor (visited-neighbors maze cell)
          :when (seq (get-in maze neighbor))]
      [cell neighbor])))

(defn create
  "Create a random maze using the Hunt and Kill algorithm."
  [grid]
  (loop [maze grid
         cell (m/random-pos maze)
         cells (remove #{cell} (m/all-positions maze))]
    (if (seq cells)
      (let [unvisited (empty-neighbors maze cell)]
        (if (seq unvisited)
          (let [neighbor (rand-nth unvisited)]
            (recur (m/link maze cell neighbor)
                   neighbor
                   (remove #{neighbor} cells)))
          (let [[hunted visited] (hunt maze cells)]
            (recur (m/link maze hunted visited)
                   hunted
                   (remove #{hunted} cells)))))
      maze)))
