(ns meiro.wilson
  "Wilson's algorithm uses a loop-erased random walk to generate an unbiased
  maze. Because it randomly navigates to the next cell without regard to
  cells it has already visited, it can take an excessively long time to
  generate, especially on large mazes."
  (:require [meiro.core :as m]))

(defn- pop-rand
  [positions]
  (let [pos (rand-nth positions)]
    (remove #{pos} positions)))

(defn- walk
  "Perform a loop-erasing random walk."
  [maze unvisited]
  (loop [pos (rand-nth unvisited)
         path [pos]]
    (if (some #{pos} unvisited)
      (let [index (.indexOf path pos)]
        (if (= -1 index)
          (recur (rand-nth (m/neighbors maze pos)) (conj path pos))
          (recur (rand-nth (m/neighbors maze pos)) (subvec path 0 (inc index)))))
      (conj path pos))))

(defn- link-path
  "Create links in the maze between each step in a path."
  [maze path]
  (reduce
    (fn [acc [pos-1 pos-2]] (m/link acc pos-1 pos-2))
    maze
    (partition 2 1 path)))

(defn create
  "Create a random maze using Wilson's algorithm."
  [grid]
  (loop [maze grid
         unvisited (pop-rand (m/all-positions grid))]
    (if (seq unvisited)
      (let [path (walk grid unvisited)]
        (recur
          (link-path maze path)
          (remove (into #{} path) unvisited)))
      maze)))
