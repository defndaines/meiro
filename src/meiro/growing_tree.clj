(ns meiro.growing-tree
  "The Growing Tree algorithm is an abstraction on generating a minimum spanning
  tree to connect cells in a maze.
  The algorithm starts from a single cell and then chooses the next node using
  the poll function provided. As new nodes are added to a forest, its
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


(defn- outside-forest
  "Retrieve the newer position from an edge.
  Returns nil if both positions in an edge are already in the nodes set.
  Will only return one position, so expect undefined behavior elsewhere if post
  positions are not in the provided nodes."
  [nodes edge]
  (first
    (remove
      (fn [pos] (contains? nodes pos))
      edge)))


(defn create
  "Create a maze with the provided dimensions.
  The `queue` manages the state of active edges and relies on the passed
  functions to manage that state.
  The `poll-fn` is a single-argument function taking the `queue`, and used
  to remove a single item from the queue. It returns the item and updated queue.
  The `shift-fn` takes three arguments, a sequence of edges, the queue, and a
  set of unqueued (remaining) edges. It transfers the edges from the unqueued
  collection to the queue."
  [width height queue poll-fn shift-fn]
  (let [node-total (* width height)
        start-pos [(rand-int width) (rand-int height)]
        every-edge (set (graph/all-edges width height))
        [start-queue start-edges] (shift-fn (pos-edges start-pos)
                                            queue
                                            every-edge)]
    (loop [forest {:width width :height height :nodes #{start-pos} :edges []}
           queue start-queue
           edges start-edges]
      ;; Maze is complete when all nodes are accounted for or queue is empty.
      (if (or (= node-total (count (:nodes forest))) (empty? queue))
        forest
        (let [[edge rest-q] (poll-fn queue)
              pos (outside-forest (:nodes forest) edge)]
          ;; Only add the edge if it links to a new position.
          (if (seq pos)
            (let [[q es] (shift-fn (pos-edges pos) rest-q edges)]
              (recur
                (-> forest
                    (update :nodes conj pos)
                    (update :edges conj edge))
                q es))
            (recur forest rest-q edges)))))))
