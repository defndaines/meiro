(ns meiro.sidewinder
  "Sidewinder algorithm is like binary-tree, but randomly chooses a position in
  a horizontal corridor to move south from when moving south. This algorithm's
  bias creates vertical paths and will always have a single corridor along
  the southern edge."
  (:require [meiro.core :as m]
            [clojure.data.generators :as gen]))


(def ^:private default-weights
  "Constants allow for different weights for each direction.
  Higher south weight has longer vertical corridors.
  Higher east weight has longer horizontal corridors."
  {:south 4 :east 5})


(defn- possible-directions
  "Determine which directions are valid from the provided pos."
  [maze pos]
  (vals
    (filter
      #(m/in? maze (first %))
      {(m/south pos) :south (m/east pos) :east})))


(defn- path-west
  "Get a path sequence of positions west of the provided position,
  including that position."
  [maze pos]
  (if (seq (filter #{:west} (get-in maze pos)))
    (cons pos (path-west maze (m/west pos)))
    [pos]))


(defn- link-fn
  "Generate a function which will link a given position to a random neighbor to
  the south or east. When linking to south, the link will be created from any
  position in the current east-west corridor, not necessarily from `pos`."
  [weights]
  (fn [maze pos]
    (let [directions (possible-directions maze pos)]
      (if (seq directions)
        (case (gen/weighted (select-keys weights directions))
          :east (m/link maze pos (m/east pos))
          :south (let [from (rand-nth (path-west maze pos))]
                   (m/link maze from (m/south from))))
        maze))))


(defn create
  "Create a random maze using the sidewinder algorithm."
  ([grid] (create grid default-weights))
  ([grid weights]
   (reduce
     (link-fn weights)
     grid
     (for [row (range (count grid)) col (range (count (first grid)))]
       [row col]))))


(defn- corridor
  "Get a path sequence of linked positions west of the provided position,
  including that position."
  [row pos]
  (cons pos
        (take-while
          (fn [pos] (= [:east] (get row pos)))
          (range (dec pos) -1 -1))))


(defn- create-row
  "Create a maze row using the Sidewinder algorithm.
  This approach does not create mutual links, but only links to the south
  or east."
  ([width weights]
   (reduce
     (fn [row pos]
       (case (gen/weighted (select-keys weights [:east :south]))
         :east (assoc row pos [:east])
         :south (let [from (rand-nth (corridor row pos))]
                  (update row from conj :south))))
     (conj (vec (repeat (dec width) [])) [:south])
     (range (dec width)))))


(defn last-row
  "Create the last row of a Sindwinder maze."
  [width]
  (conj
    (vec (repeat (dec width) [:east]))
    [:west]))


(defn create-lazy
  "Create a potentially infinite Sidewinder maze."
  ([width] (create-lazy width default-weights))
  ([width weights]
   (cons (create-row width weights)
         (lazy-seq (create-lazy width weights)))))
