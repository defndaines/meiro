(ns meiro.hunt-and-kill
  "Hunt and Kill performs a random walk against unvisited cells, then searches
  for the first unvisited cell adjacent to a visited cell to link and continue,
  until all cells have been visited."
  (:require [meiro.core :as m]))

;; TODO This is different from the same named function elsewhere. May rethink.
(defn- empty-neighbors
  "Get all positions neighboring `pos` which have not been visited."
  [maze pos]
  (filter #(empty? (get-in maze %)) (m/neighbors maze pos)))

(defn- visited-neighbors
  "Get all positions neighboring `pos` which have been visited."
  [maze pos]
  (remove #(empty? (get-in maze %)) (m/neighbors maze pos)))

(defn- hunt
  "Find first unvisited position with a visited neighbor."
  [maze positions]
  (first
    (for [pos positions
          neighbor (visited-neighbors maze pos)
          :when (seq (get-in maze neighbor))]
      [pos neighbor])))

(defn create
  "Create a random maze using the Hunt and Kill algorithm."
  [grid]
  (loop [maze grid
         pos (m/random-pos maze)
         positions (remove #{pos} (m/all-positions maze))]
    (if (seq positions)
      (let [unvisited (empty-neighbors maze pos)]
        (if (seq unvisited)
          (let [neighbor (rand-nth unvisited)]
            (recur (m/link maze pos neighbor)
                   neighbor
                   (remove #{neighbor} positions)))
          (let [[hunted visited] (hunt maze positions)]
            (recur (m/link maze hunted visited)
                   hunted
                   (remove #{hunted} positions)))))
      maze)))
