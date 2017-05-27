(ns meiro.core
  "Core maze-generation utilities.
  Mazes are represented as a vector of vectors, which can be accessed by
  [row column]. In a fully generated maze, each cell will contain the directions
  to open neighbor cells, e.g., [:east :south]."
  (:require [clojure.spec.alpha :as spec]))

;; Difference between a "cell" and "pos" (or position).
;; A position is the [row col] index of a cell.
(spec/def ::pos (spec/cat :row nat-int? :col nat-int?))
(spec/def ::path (spec/+ ::pos))
;; A cell has links to neighbors, such as [:east :north].
(spec/def ::direction #{:north :south :east :west})
(spec/def ::cell (spec/coll-of ::direction :kind vector? :distinct true))

;; Difference between "grid" and "maze"?
;; A grid has no links, a maze has links between the cells.
(spec/def ::grid (spec/coll-of vector? :kind vector? :min-count 1))
(spec/def ::row (spec/coll-of ::cell :kind vector? :min-count 1))
(spec/def ::maze (spec/coll-of ::row :kind vector? :min-count 1))

;;; Position Functions

(spec/fdef adjacent?
  :args (spec/cat :pos-1 ::pos :pos-2 ::pos)
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

(spec/fdef direction
  :args (spec/cat :pos-1 ::pos :pos-2 ::pos)
  :ret (spec/nilable ::direction))
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

(spec/fdef north
  :args (spec/cat :pos ::pos)
  :ret ::pos)
(defn north
  "Get position to the north of a given position.
  No bounds checking, so may return an invalid position."
  [pos]
  (let [[row col] pos]
    [(dec row) col]))

(spec/fdef south
  :args (spec/cat :pos ::pos)
  :ret ::pos)
(defn south
  "Get position to the south of a given position.
  No bounds checking, so may return an invalid position."
  [pos]
  (let [[row col] pos]
    [(inc row) col]))

(spec/fdef east
  :args (spec/cat :pos ::pos)
  :ret ::pos)
(defn east
  "Get position to the east of a given position.
  No bounds checking, so may return an invalid position."
  [pos]
  (let [[row col] pos]
    [row (inc col)]))

(spec/fdef west
  :args (spec/cat :pos ::pos)
  :ret ::pos)
(defn west
  "Get position to the west of a given position.
  No bounds checking, so may return an invalid position."
  [pos]
  (let [[row col] pos]
    [row (dec col)]))

(spec/fdef pos-to
  :args (spec/cat :cardinal ::direction :pos ::pos)
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

;;; Grid Functions

(spec/fdef init
  :args (spec/cat :rows pos-int? :columns pos-int? :v (spec/? any?))
  :ret ::grid)
(defn init
  "Initialize a grid of cells with the given number of rows and columns,
  which can be accessed by index. Conceptually, [0 0] is the upper left corner."
  ([rows columns] (init rows columns []))
  ([rows columns v]
   (vec (repeat rows
                (vec (repeat columns v))))))

(spec/fdef in?
  :args (spec/cat :grid ::grid :pos ::pos)
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

(spec/fdef neighbors
  :args (spec/cat :grid ::grid :pos ::pos)
  :ret (spec/coll-of ::pos)
  :fn #(every? adjacent? %))
(defn neighbors
  "Get all potential neighbors of a position in a given grid"
  [grid pos]
  (let [[row col] pos]
    (filter
      #(in? grid %)
      #{[(dec row) col] [(inc row) col] [row (dec col)] [row (inc col)]})))

(spec/fdef all-positions
  :args (spec/cat :grid ::grid)
  :ret (spec/coll-of ::pos :min-count 2)
  :fn #(every? (fn [pos] (in? (-> % :args :grid) pos)) (:ret %)))
(defn all-positions
  "Get a sequence of all the positions in a grid."
  [grid]
  ;; Maybe put into set, but then would have to figure out how to
  ;; randomly select from set.
  (for [row (range (count grid))
        col (range (count (first grid)))]
    [row col]))

(spec/fdef random-pos
  :args (spec/cat :grid ::grid)
  :ret ::pos
  :fn #(in? (-> % :args :grid) (:ret %)))
(defn random-pos
  "Select a random position from the grid."
  [grid]
  [(rand-int (count grid)) (rand-int (count (first grid)))])

;;; Maze Functions

(spec/fdef path-west
  :args (spec/cat :maze ::maze :pos ::pos)
  :ret ::path
  :fn #(= 1 (count (reduce (fn [acc [col _]] (conj acc col)) #{} %))))
(defn path-west
  "Get a path sequence of positions west of the provided position,
  including that position."
  [maze pos]
  (if (seq (filter #{:west} (get-in maze pos)))
    (cons pos (path-west maze (west pos)))
    [pos]))

;; TODO This is different from the same named function defined in dijkstra.
;;      May rethink.
(spec/fdef empty-neighbors
  :args (spec/cat :maze ::maze :pos ::pos)
  :ret (spec/coll-of ::pos)
  :fn #(every? adjacent? %))
(defn empty-neighbors
  "Get all positions neighboring `pos` which have not been visited."
  [maze pos]
  (filter #(empty? (get-in maze %)) (neighbors maze pos)))

(spec/fdef link
  :args (spec/cat :maze ::maze :pos-1 ::pos :pos-2 ::pos)
  :ret ::maze
  :fn #(if (adjacent? (-> % :args :pos-1) (-> % :args :pos-2))
         (and
           (not (empty?) (get-in (:ret %) (-> % :args :pos-1)))
           (not (empty?) (get-in (:ret %) (-> % :args :pos-2))))))
(defn link
  "Link two adjacent cells in a maze."
  [maze pos-1 pos-2]
  (if (and (adjacent? pos-1 pos-2) (in? maze pos-1) (in? maze pos-2))
    (-> maze
        (update-in pos-1 conj (direction pos-1 pos-2))
        (update-in pos-2 conj (direction pos-2 pos-1)))
    maze))

(spec/fdef dead-ends
  :args (spec/cat :maze ::maze)
  :ret int?)
(defn dead-ends
  "Filter for the dead ends in a maze.
  Fewer dead ends contribute to 'river', more flowing and meandering in a maze."
  [maze]
  (mapcat
    (fn [row] (filter #(= 1 (count %)) row))
    maze))
