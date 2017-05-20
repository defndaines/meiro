(ns meiro.core
  "Core maze-generation utilities.
  Mazes are represented as a vector of vectors, which can be accessed by
  [row column]. In a fully generated maze, each cell will contain the directions
  to open neighbor cells, e.g., [:east :south]."
  (:require [clojure.spec.alpha :as s]))

;; Difference between a "cell" and "pos" (or position).
;; A position is the [row col] index of a cell.
(s/def ::pos (s/cat :row nat-int? :col nat-int?))
;; A cell has links to neighbors, such as [:east :north].
(s/def ::direction #{:north :south :east :west})
(s/def ::cell (s/coll-of ::direction :kind vector? :distinct true))

;; Difference between "grid" and "maze"?
;; A grid has no links, a maze has links between the cells.
(s/def ::grid (s/coll-of vector? :kind vector? :min-count 1))
(s/def ::row (s/coll-of ::cell :kind vector? :min-count 1))
(s/def ::maze (s/coll-of ::row :kind vector? :min-count 1))

;; TODO May want to break apart code into "grid" and "maze" namespaces.

(defn init
  "Initialize a grid of cells with the given number of rows and columns,
  which can be accessed by index. Conceptually, [0 0] is the upper left corner."
  ([rows columns] (init rows columns []))
  ([rows columns v]
   (vec (repeat rows
                (vec (repeat columns v))))))

(s/fdef in?
  :args (s/cat :grid ::grid :pos ::pos)
  :ret boolean?)
(defn in?
  "Is the position within the bounds of the grid."
  [grid pos]
  (let [max-row (dec (count grid))
        max-col (dec (count (first grid)))
        [row col] pos]
    (and
      (<= 0 row max-row)
      (<= 0 col max-col))))

(s/fdef adjacent?
  :args (s/cat :pos-1 ::pos :pos-2 ::pos)
  :ret boolean?)
(defn adjacent?
  "Are two positions adjacent.
  Function does not check that positions are within the bounds of a grid."
  [pos-1 pos-2]
  (let [[row-1 col-1] pos-1
        [row-2 col-2] pos-2]
    (or
      (and (= row-1 row-2) (= 1 (Math/abs (- col-1 col-2))))
      (and (= col-1 col-2) (= 1 (Math/abs (- row-1 row-2)))))))

(s/fdef direction
  :args (s/cat :pos-1 ::pos :pos-2 ::pos)
  :ret (s/nilable ::direction))
(defn direction
  "Get the direction from pos-1 to pos-2.
  Assumes [0 0] is the north-west corner."
  [pos-1 pos-2]
  (let [[row-1 col-1] pos-1 [row-2 col-2] pos-2]
    (case [(- row-1 row-2) (- col-1 col-2)]
      [0 1] :west
      [0 -1] :east
      [1 0] :north
      [-1 0] :south
      nil)))

;; TODO Unused. Here for "completeness", but by never be used.
(s/fdef north
  :args (s/cat :pos ::pos)
  :ret ::pos)
(defn north
  "Get position to the north of a given position.
  No bounds checking, so may return an invalid position."
  [pos]
  (let [[row col] pos]
    [(dec row) col]))

(s/fdef south
  :args (s/cat :pos ::pos)
  :ret ::pos)
(defn south
  "Get position to the south of a given position.
  No bounds checking, so may return an invalid position."
  [pos]
  (let [[row col] pos]
    [(inc row) col]))

(s/fdef east
  :args (s/cat :pos ::pos)
  :ret ::pos)
(defn east
  "Get position to the east of a given position.
  No bounds checking, so may return an invalid position."
  [pos]
  (let [[row col] pos]
    [row (inc col)]))

(s/fdef west
  :args (s/cat :pos ::pos)
  :ret ::pos)
(defn west
  "Get position to the west of a given position.
  No bounds checking, so may return an invalid position."
  [pos]
  (let [[row col] pos]
    [row (dec col)]))

(s/fdef pos-to
  :args (s/cat :cardinal ::direction :pos ::pos)
  :ret ::pos
  :fn #(adjacent? (:ret %) (-> % :args :pos)))
(defn pos-to
  "Get neighboring position given a direction.
  No bounds checking, so may return invalid position."
  [cardinal pos]
  (let [[row col] pos]
    (case cardinal
      :north [(dec row) col]
      :south [(inc row) col]
      :east [row (inc col)]
      :west [row (dec col)])))

(defn path-west
  "Get a path sequence of positions west of the provided position,
  including that position."
  [maze pos]
  (if (seq (filter #{:west} (get-in maze pos)))
    (cons pos (path-west maze (west pos)))
    [pos]))

(defn neighbors
  "Get all potential neighbors of a position in a given grid"
  [grid pos]
  (let [[row col] pos]
    (filter
      #(in? grid %)
      #{[(dec row) col] [(inc row) col] [row (dec col)] [row (inc col)]})))

(defn all-positions
  "Get a sequence of all the positions in a grid."
  [grid]
  ;; Maybe put into set, but then would have to figure out how to
  ;; randomly select from set.
  (for [row (range (count grid))
        col (range (count (first grid)))]
    [row col]))

(defn random-pos
  "Select a random position from the grid."
  [grid]
  [(rand-int (count grid)) (rand-int (count (first grid)))])

;; TODO This is different from the same named function defined in dijkstra.
;;      May rethink.
(defn empty-neighbors
  "Get all positions neighboring `pos` which have not been visited."
  [maze pos]
  (filter #(empty? (get-in maze %)) (neighbors maze pos)))

(defn link
  "Link two adjacent cells in a maze."
  [maze pos-1 pos-2]
  (if (and (adjacent? pos-1 pos-2) (in? maze pos-1) (in? maze pos-2)) 
    (-> maze
        (update-in pos-1 conj (direction pos-1 pos-2))
        (update-in pos-2 conj (direction pos-2 pos-1)))
    maze))

(defn dead-ends
  "Filter for the dead ends in a maze.
  Fewer dead ends contribute to 'river', more flowing and meandering in a maze."
  [maze]
  (mapcat
    (fn [row] (filter #(= 1 (count %)) row))
    maze))
