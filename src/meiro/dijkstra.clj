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


(defn distances
  "Calculate distances to each pos relative from starting pos.
  Assumes a perfect maze."
  ([maze] (distances maze [0 0]))
  ([maze pos]
   (distances maze pos (m/init (count maze) (count (first maze)) nil) 0))
  ([maze pos acc dist]
   (if (seq pos)
     (reduce
       #(distances maze %2 %1 (inc dist))
       (assoc-in acc pos dist)
       (empty-neighbors acc pos (get-in maze pos)))
     acc)))


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
