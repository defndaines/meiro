(ns meiro.graph
  "Graph-based functions for creating mazes.
  While most of the functions in the core namespace use a position-aware grid,
  these functions work in direct terms of nodes and edges."
  (:require [meiro.core :as m]
            [meiro.weave :as w]
            [clojure.set]
            [clojure.spec.alpha :as spec]))


;; Nodes (cells) are [x y] coordinates, but y increases as it goes down.
;; Treat [0 0] as the northwest corner of a grid or maze.
(spec/def ::node (spec/tuple :row nat-int? :col nat-int?))

;; Edges are a pair of nodes. They need not be adjacent.
(spec/def ::edge (spec/tuple ::node :kind vector? :count 2 :distinct true))

;; Forests are a map of nodes and edges. A spanning tree is complete when there
;; is only one forest remaining.
(spec/def ::width pos-int?)
(spec/def ::height pos-int?)
(spec/def ::nodes (spec/coll-of ::node :kind set?))
(spec/def ::edges (spec/coll-of :edge :kind vector?))
(spec/def ::forest (spec/keys :req-un [::width ::height ::nodes ::edges]))


(spec/fdef all-edges
  :args (spec/cat :width ::width :height ::height)
  :ret ::edges)
(defn all-edges
  "Get all edges in a grid given a width and height."
  [width height]
  (concat
    (for [x (range (dec width)) y (range height)]
      [[x y] [(inc x) y]])
    (for [x (range width) y (range (dec height))]
      [[x y] [x (inc y)]])))


(spec/fdef init-forests
  :args (spec/cat :width ::width :height ::height)
  :ret ::forest)
(defn init-forests
  "Get all the nodes in a grid and put them into forest maps."
  [width height]
  (reduce
    (fn [acc e] (conj acc {:width width :height height :nodes #{e} :edges []}))
    #{}
    (for [x (range width) y (range height)] [x y])))


(spec/fdef find-forest
  :args (spec/cat :forests ::forest :pos ::node)
  :ret ::forest)
(defn find-forest
  "Get the forest containing the position."
  [forests pos]
  (first
    (filter
      (fn [f] (contains? (:nodes f) pos))
      forests)))


(spec/fdef merge-forests
  :args (spec/cat :f-1 ::forest :f-2 ::forest :edge ::edge)
  :ret ::forest)
(defn merge-forests
  "Merge two forests into forest set."
  [f-1 f-2 edge]
  (let [{ns-1 :nodes es-1 :edges} f-1
        {ns-2 :nodes es-2 :edges} f-2]
    {:width (or (:width f-1) (:width f-2))
     :height (or (:height f-1) (:height f-2))
     :nodes (clojure.set/union ns-1 ns-2)
     :edges (conj (into es-1 es-2) edge)}))


(spec/fdef forest-to-maze
  :args (spec/cat :forest ::forest)
  :ret :meiro.core/maze)
(defn forest-to-maze
  "Convert a forest map to the standard maze format used in most PNG functions."
  [forest]
  (reduce
    (fn [maze [[x y] [x' y']]]
      (w/link maze [y x] [y' x']))
    (m/init (:height forest) (:width forest))
    (:edges forest)))
