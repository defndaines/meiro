(ns meiro.eller
  "Eller's algorithm generates a forest row-by-row, combining all the forests at
  the end into a single result. It behaves like Sidewinder in that it can merge
  cells left to right, but then only one passage will link south."
  (:require [meiro.core :as m]
            [meiro.graph :as graph]))


(defn for-forests
  "Get the forests for a row. If there is no forest yet for a node in the row,
  a new forest will be generated."
  [forests row width height]
  (reduce
    (fn [acc pos]
      (if-let [forest (graph/find-forest acc pos)]
        acc
        (conj acc {:width width :height height :nodes #{pos} :edges []})))
    forests
    (for [col (range width)] [col row])))


(defn merge-all
  "Merge all forests by finding an adjacent node in another forest to link."
  [forests]
  (reduce
    (fn [acc e]
      (let [forest (graph/find-forest forests e)
            node (first (shuffle (:nodes forest)))
            neighbors (filter #(m/adjacent? node %) (:nodes acc))]
        (if (map? acc)
          (let [pos (first (shuffle neighbors))]
            (graph/merge-forests acc forest [pos node]))
          forest)))
    nil
    (for [x (range (:width (first forests)))]
      [x (dec (:height (first forests)))])))


(defn create
  "Create a maze using Eller's algorithm. Returns a forest."
  [width height]
  (loop [rows height
         forests []]
    (if (zero? rows)
      (merge-all forests)
      ()
      )))
