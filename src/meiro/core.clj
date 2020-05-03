(ns meiro.core
  "Core maze-generation utilities.
  Mazes are represented as a vector of vectors, which can be accessed by
  [row column]. In a fully generated maze, each cell will contain the directions
  to open neighbor cells, e.g., [:east :south]."
  (:require [clojure.spec.alpha :as spec]))


(def cardinals
  "Cardinal directions."
  #{:north :south :east :west})

(spec/def ::direction cardinals)
(spec/def ::link (conj cardinals :mask))

;; Difference between a "cell" and "pos" (or position).
;; A position is the [row col] index of a cell.
(spec/def ::pos (spec/tuple :row nat-int? :col nat-int?))
(spec/def ::path (spec/+ ::pos))

;; A cell has links to neighbors, such as [:east :north].
(spec/def ::cell (spec/coll-of ::link :kind vector? :distinct true))

;; Difference between "grid" and "maze"?
;; A grid has no links, a maze has links between the cells.
(spec/def ::grid (spec/coll-of vector? :kind vector? :min-count 1))
(spec/def ::row (spec/coll-of ::cell :kind vector? :min-count 1))
(spec/def ::maze (spec/coll-of ::row :kind vector? :min-count 1))

(spec/def ::rate (spec/double-in :min 0.0 :max 1.0 :NaN? false :infinite? false))


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
      (and (= row-1 row-2) (= 1 (Math/abs ^int (- col-1 col-2))))
      (and (= col-1 col-2) (= 1 (Math/abs ^int (- row-1 row-2)))))))


(spec/fdef direction
  :args (spec/cat :pos-1 ::pos :pos-2 ::pos)
  :ret (spec/nilable ::direction))
(defn direction
  "Get the direction from pos-1 to pos-2.
  Assumes [0 0] is the north-west corner."
  [[row-1 col-1] [row-2 col-2]]
  (case [(- row-1 row-2) (- col-1 col-2)]
    [0 1] :west
    [0 -1] :east
    [1 0] :north
    [-1 0] :south
    nil))


(spec/fdef north
  :args (spec/cat :pos ::pos)
  :ret ::pos)
(defn north
  "Get position to the north of a given position.
  No bounds checking, so may return an invalid position."
  [[row col]]
  [(dec row) col])


(spec/fdef south
  :args (spec/cat :pos ::pos)
  :ret ::pos)
(defn south
  "Get position to the south of a given position.
  No bounds checking, so may return an invalid position."
  [[row col]]
  [(inc row) col])


(spec/fdef east
  :args (spec/cat :pos ::pos)
  :ret ::pos)
(defn east
  "Get position to the east of a given position.
  No bounds checking, so may return an invalid position."
  [[row col]]
  [row (inc col)])


(spec/fdef west
  :args (spec/cat :pos ::pos)
  :ret ::pos)
(defn west
  "Get position to the west of a given position.
  No bounds checking, so may return an invalid position."
  [[row col]]
  [row (dec col)])


(spec/fdef pos-to
  :args (spec/cat :cardinal ::direction :pos ::pos)
  :ret ::pos
  :fn #(adjacent? (:ret %) (-> % :args :pos)))
(defn pos-to
  "Get neighboring position given a direction.
  No bounds checking, so may return invalid position."
  [cardinal [row col]]
  (case cardinal
    :north [(dec row) col]
    :south [(inc row) col]
    :east [row (inc col)]
    :west [row (dec col)]))


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
  (if (seq pos)
    (let [[row col] pos
          max-row (dec (count grid))
          max-col (dec (count (get grid row)))]
      (and
        (<= 0 row max-row)
        (<= 0 col max-col)))
    false))


(spec/fdef neighbors
  :args (spec/cat :grid ::grid :pos ::pos)
  :ret (spec/coll-of ::pos)
  :fn #(every? adjacent? %))
(defn neighbors
  "Get all potential neighbors of a position in a given grid"
  [grid [row col]]
  (filter
    #(in? grid %)
    #{[(dec row) col] [(inc row) col] [row (dec col)] [row (inc col)]}))


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
  "Select a random position from the grid.
  Will not return a masked cell if there are any in the grid."
  [grid]
  (let [pos [(rand-int (count grid)) (rand-int (count (first grid)))]]
    ;; When grids contain masked cells, make sure to return an unmasked cell.
    (if (not-any? #{:mask} (get-in grid pos))
      pos
      (random-pos grid))))


;;; Maze Functions

(spec/fdef empty-neighbors
  :args (spec/alt
          :2-args (spec/cat :maze ::maze :pos ::pos)
          ;; The 3-arg ::maze can contain
          ;;   #{::pos :north :south :east :west
          ;;     :northeast :northwest :southeast :southwest
          ;;     :inward :clockwise :counter-clockwise :mask}}
          :3-args (spec/cat :maze ::maze :neighbor-fn ifn? :pos ::pos))
  :ret (spec/coll-of ::pos)
  :fn #(every? adjacent? %))
(defn empty-neighbors
  "Get all positions neighboring `pos` which have not been visited."
  ([maze pos] (empty-neighbors maze neighbors pos))
  ([maze neighbor-fn pos]
   (filter #(empty? (get-in maze %)) (neighbor-fn maze pos))))


(spec/fdef link-with
  :args (spec/cat :direction-fn ifn?)
  :ret ifn?)
(defn link-with
  "Create a link function which uses the provided direction-fn."
  [direction-fn]
  (fn [maze pos-1 pos-2]
    (-> maze
        (update-in pos-1 conj (direction-fn pos-1 pos-2))
        (update-in pos-2 conj (direction-fn pos-2 pos-1)))))


(spec/fdef link
  :args (spec/cat :maze ::maze :pos-1 ::pos :pos-2 ::pos)
  :ret ::maze
  :fn #(and
         (seq (get-in (:ret %) (-> % :args :pos-1)))
         (seq (get-in (:ret %) (-> % :args :pos-2)))))
(defn link
  "Link two adjacent cells in a maze."
  ([maze pos-1 pos-2]
   ((link-with direction) maze pos-1 pos-2)))


(spec/fdef dead-ends
  :args (spec/cat :maze ::maze)
  :ret (spec/coll-of ::pos))
(defn dead-ends
  "Filter for the dead ends in a maze.
  Fewer dead ends contribute to 'river', more flowing and meandering in a maze."
  [maze]
  (for [[y row] (map-indexed vector maze)
        [x cell] (map-indexed vector row)
        :when (and (= 1 (count cell)) (not= [:mask] cell))]
    [y x]))


(spec/fdef braid
  :args (spec/cat :maze ::maze :rate (spec/? ::rate))
  :ret ::maze)
(defn braid
  "Braid a maze.
  Braiding introduces loops into a maze by removing dead ends. A rate can be
  passed in to indicate a percentage of dead ends to link, otherwise all dead
  end will be linked to an adjacent cell."
  ([maze] (braid maze 1.0))
  ([maze rate]
   (loop [acc maze
          positions (dead-ends maze)]
     (if (seq positions)
       (if (> rate (rand))
         (let [pos (first positions)
               adj (filter #(adjacent? pos %) (rest positions))
               neighbor (if (seq adj)
                          (rand-nth adj)
                          (rand-nth (neighbors maze pos)))
               remaining (if (seq adj)
                           (remove #{neighbor} (rest positions))
                           (rest positions))]
           (recur (link acc pos neighbor) remaining))
         (recur acc (rest positions)))
       acc))))


(spec/fdef unlink
  :args (spec/cat :maze ::maze :pos-1 ::pos :pos-2 ::pos)
  :ret ::maze)
(defn unlink
  "Unlink two cells in a maze. Will replace dead ends with a mask to facilitate
  rendering."
  [maze pos-1 pos-2]
  (let [cell-1 (get-in maze pos-1)
        cell-2 (get-in maze pos-2)]
    (-> maze
        (assoc-in
          pos-1
          (if (= 1 (count cell-1))
            [:mask]
            (vec (remove #{(direction pos-1 pos-2)} (get-in maze pos-1)))))
        (assoc-in
          pos-2
          (if (= 1 (count cell-2))
            [:mask]
            (vec (remove #{(direction pos-2 pos-1)} (get-in maze pos-2))))))))


(spec/fdef cull
  :args (spec/cat :maze ::maze :rate (spec/? ::rate))
  :ret ::maze)
(defn cull
  "Cull dead ends from a maze by unlinking them.
  This results in a sparse maze. A rate can be passed in to indicate a
  percentage of dead ends to unlink, otherwise all dead end will be unlinked."
  ([maze] (cull maze 1.0))
  ([maze rate]
   (loop [acc maze
          positions (dead-ends maze)]
     (if (seq positions)
       (if (> rate (rand))
         (let [pos (first positions)
               neighbor (pos-to (first (get-in maze pos)) pos)]
           (recur
             (unlink acc pos neighbor)
             (rest positions)))
         (recur acc (rest positions)))
       acc))))
