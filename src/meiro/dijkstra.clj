(ns meiro.dijkstra
  "Dijkstra's algorithm for measuring distances. This in turn can be used to
  find solutions as well as shortest and longest paths."
  (:require [meiro.core :as m]))


(defn- empty-neighbors
  "Given a grid of distances, find neighbors of a pos which haven't been
  calculated yet."
  [grid pos neighbors]
  (filter
    #(nil? (get-in grid %))
    (map #(m/pos-to % pos) neighbors)))


(defn- init-distances
  "Initialize the distance calculation grid."
  [grid]
  (m/init (count grid) (count (first grid)) nil))


(defn distances
  "Calculate distances to each pos relative from starting pos.
  Assumes a perfect maze."
  ([maze] (distances maze [0 0]))
  ([maze pos]
   (distances maze pos (init-distances maze) 0))
  ([maze pos acc dist]
   (if (seq pos)
     (reduce
       #(distances maze %2 %1 (inc dist))
       (assoc-in acc pos dist)
       (empty-neighbors acc pos (get-in maze pos)))
     acc)))


(defn distances-by-breadth
  "Calculate distances to each position relative from the starting position
  (defaults to [0 0] if not provided. Does a breadth-first search, so it can
  handle non-perfect mazes or mazes with rooms."
  ([maze] (distances-by-breadth maze [0 0]))
  ([maze pos]
   (loop [neighbors (list pos)
          acc (init-distances maze)
          dist 0]
     (if (seq neighbors)
       (let [level (reduce (fn [a e] (assoc-in a e dist)) acc neighbors)]
         (recur
           (set (mapcat
                  (fn [pos] (empty-neighbors level pos (get-in maze pos)))
                  neighbors))
           level
           (inc dist)))
       acc))))


(defn solution
  "Provide the path between to two cells in the maze."
  [maze start end]
  (let [dist (distances maze start)
        step (fn [pos n]
               (last (filter #(= (dec n) (get-in dist %))
                             (map #(m/pos-to % pos) (get-in maze pos)))))]
    (loop [acc '()
           pos end]
      (let [n (get-in dist pos)]
        (if (zero? n)
          (conj acc pos)
          (recur (conj acc pos) (step pos n)))))))


(defn shortest-path
  "Get the shortest path between two positions in a maze.
  Accommodates non-perfect mazes."
  ([maze start end]
   (let [dist (distances-by-breadth maze start)
         step (fn [pos n]
                (filter #(= (dec n) (get-in dist %))
                        (map #(m/pos-to % pos) (get-in maze pos))))]
     ((fn get-shorty [paths]
        (let [n (get-in dist (ffirst paths))]
          (if (zero? n)
            (first paths)
            (get-shorty
              (reduce
                (fn [acc [head &_ :as path]]
                  (let [steps (step head n)]
                    (if (seq steps)
                      (map (fn [e] (conj path e)) steps)
                      acc)))
                '() paths)))))
      (list (list end))))))


(defn farthest-pos
  "Find the farthest position from a given position, using [0 0] if none is
  provided."
  ([maze] (farthest-pos maze [0 0]))
  ([maze pos]
   (let [dist (distances maze pos)]
     (second
       (last
         (sort-by first
                  (for [[x row] (map-indexed vector dist)
                        [y v] (map-indexed vector row)]
                    [v [x y]])))))))


(defn longest-path
  "Provide the path between the cells farthest apart in the maze."
  [maze]
  (let [farthest (farthest-pos maze)
        and-back (farthest-pos maze farthest)]
    (solution maze and-back farthest)))
