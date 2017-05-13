(ns meiro.wilson
  "Wilson's algorithm uses a loop-erased random walk to generate an unbiased
  maze. Because it randomly navigates to the next cell without regard to
  cells it has already visited, it can take an excessively long time to
  generate, especially on large mazes."
  (:require [meiro.core :as m]))

(defn- pop-rand
  [cells]
  (let [cell (rand-nth cells)]
    (remove #{cell} cells)))

(defn- walk
  "Perform a loop-erasing random walk."
  [maze unvisited]
  (loop [cell (rand-nth unvisited)
         path [cell]]
    (if (some #{cell} unvisited)
      (let [pos (.indexOf path cell)]
        (if (= -1 pos)
          (recur (rand-nth (m/neighbors maze cell)) (conj path cell))
          (recur (rand-nth (m/neighbors maze cell)) (subvec path 0 (inc pos)))))
      (conj path cell))))

(defn- link-path
  "Create links in the maze between each step in a path."
  [maze path]
  (reduce
    (fn [acc [c1 c2]] (m/link acc c1 c2))
    maze
    (partition 2 1 path)))

(defn create
  "Create a random maze using Wilson's algorithm."
  [maze]
  (loop [acc maze
         unvisited (pop-rand (m/all-positions maze))]
    (if (seq unvisited)
      (let [path (walk maze unvisited)]
        (recur
          (link-path acc path)
          (remove (into #{} path) unvisited)))
      acc)))
