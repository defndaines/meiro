(ns meiro.sidewinder
  (:require [meiro.core :as m]
            [clojure.data.generators :as gen]))

;; Allows for different weights for going each direction.
;; Higher south weight has longer verticle corridors.
;; Higher east weight has longer horizontal corridors.
(def weights {:south 4 :east 5})

(defn possible-directions
  "Determine which directions are valid from the provided cell."
  [maze cell]
  (vals
    (filter
      #(m/in? maze (first %))
      {(m/south cell) :south (m/east cell) :east})))

(defn- link-neighbor
  [maze cell]
  (let [directions (possible-directions maze cell)]
    (if (seq directions)
      (case (gen/weighted (select-keys weights directions))
        :east (m/link maze cell (m/east cell))
        :south (let [from (rand-nth (m/cells-west maze cell))]
                 (m/link maze from (m/south from))))
      maze)))

(defn create
  "Create a random maze using the sidewinder algorithm."
  [maze]
  (reduce
    link-neighbor
    maze
    (for [row (range (count maze)) col (range (count (first maze)))]
      [row col])))
