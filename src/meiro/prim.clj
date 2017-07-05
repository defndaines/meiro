(ns meiro.prim
  "Prims's algorithm uses a minimum spanning tree to connect cells in a maze.
  The algorithm starts from a single cell and then chooses the next node based
  upon the cost of available edges. As new nodes are added to a forest, its
  edges are added to the available edges until the tree is complete."
  (:require [meiro.core :as m]
            [meiro.graph :as graph]))


;; NOTE: The code assumes that edges are always given in ascending order.
;;       For example, [[0 1] [0 2]] and not [[0 2] [0 1]].


(defn- pos-edges
  "Get all edges to a given position. Does not check for validity."
  [[x y :as pos]]
  [[[x (dec y)] pos]
   [pos [x (inc y)]]
   [pos [(inc x) y]]
   [[(dec x) y] pos]])


(defn- newer-pos
  "Retrieve the newer position from an edge.
  Returns nil if both positions in an edge are already in the nodes set.
  Will only return one position, so expect undefined behavior elsewhere if post
  positions are not in the provided nodes."
  [nodes edge]
  (first
    (remove
      (fn [pos] (contains? nodes pos))
      edge)))


(defn poll
  "Retrieves and removes the head of the queue, or returns nil if this queue is
  empty.
  Wrapper function to abstract PriorityQueue interface."
  [queue]
  (let [[_ edge] (.poll queue)]
    [edge queue]))


(defn to-active!
  "Move new edges to the queue, removing them from remaining edges.
  It is expected that the queue is a mutable Java PriorityQueue and will modify
  the state of the queue."
  [new-edges queue remaining-edges]
  (reduce
    (fn [[q es] e]
      (let [remaining (disj es e)]
        (if (= es remaining)  ; edge was already used or is invalid
          [q es]
          [(do
             (.offer q [(rand-int 10) e])
             q)
           remaining])))
    [queue remaining-edges]
    new-edges))


(defn create
  "Create a maze with the provided dimensions using Prim's algorithm."
  [width height]
  (let [node-total (* width height)
        start-pos [(rand-int width) (rand-int height)]
        every-edge (set (graph/all-edges width height))
        [start-queue start-edges] (to-active! (pos-edges start-pos)
                                              (java.util.PriorityQueue.)
                                              every-edge)]
    (loop [forest {:width width :height height :nodes #{start-pos} :edges []}
           queue start-queue
           edges start-edges]
      ;; Maze is complete when all nodes are accounted for or queue is empty.
      (if (or (= node-total (count (:nodes forest))) (empty? queue))
        forest
        (let [[edge rest-q] (poll queue)
              pos (newer-pos (:nodes forest) edge)]
          ;; Only add the edge if it links to a new position.
          (if (seq pos)
            (let [[q es] (to-active! (pos-edges pos) rest-q edges)]
              (recur
                (-> forest
                    (update :nodes conj pos)
                    (update :edges conj edge))
                q es))
            (recur forest queue edges)))))))
