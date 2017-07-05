(ns meiro.kruskal
  "Kruskal's algorithm uses a minimum spanning tree to connect cells in a maze.
  Although spanning trees typically have weights to edges to resolve paths,
  using a random sort to the edges can achieve the effect we need to create a
  maze.
  This algorithm is not designed to work with the other approaches.
  Instead of using a row-column arrangement, this algorithm uses x y
  coordinates aligned with the needs of a renderer."
  (:require [meiro.core :as m]
            [meiro.graph :as graph]
            [meiro.weave :as w]))


(defn- partition-edges
  "Partition non-adjacent edges from a weave."
  [non-adj]
  (reduce
    (fn [acc [pos-1 pos-2]]
      (concat
        acc
        (partition
          2 1
          (concat [pos-1]
                  (w/positions-between pos-1 pos-2)
                  [pos-2]))))
    [] non-adj))


(defn- weave-edges
  "Get all the edges associated with a weave from a collection of forests."
  [forests]
  (partition-edges
    (reduce
      (fn [acc e]
        (let [es (:edges e)]
          (concat
            acc
            (filter (fn [[a b]] (not (m/adjacent? a b))) es))))
      [] forests)))


(defn- rm-weave-edges
  "Remove non-adjacent edges to prevent invalid links against a weave."
  [edges weave-edges]
  (reduce
    (fn [acc e]
      (if (some #{e} weave-edges)
        acc
        (conj acc e)))
    [] edges))


(defn create
  "Create a maze with the provided dimensions."
  ([width height] (create width height (graph/init-forests width height)))
  ([width height forests]
   (loop [forests forests
          edges (shuffle
                  (rm-weave-edges
                    (graph/all-edges width height)
                    (weave-edges forests)))]
     (if (> (count forests) 1)
       (let [[pos-1 pos-2 :as edge] (first edges)
             f-1 (graph/find-forest forests pos-1)
             f-2 (graph/find-forest forests pos-2)]
         (recur
           (if (= f-1 f-2)
             forests
             (let [merged (graph/merge-forests f-1 f-2 edge)]
               (-> forests
                   (disj f-1 f-2)
                   (conj merged))))
           (rest edges)))
       (first forests)))))


(defn- can-weave?
  "Are the given forests eligible to weave?"
  [north south east west middle dir]
  (not
    (or
      (some nil? [north south east west])
      (= east north)
      (= east south)
      (= west north)
      (= west south)
      (if (= dir :vertical)
        (or (= middle east) (= middle west))
        (or (= middle north) (= middle south))))))


(defn weave
  "Add a weave to the forests centered on the provided `pos`.
  A direction can also be passed, either `:horizontal` or `:vertical`.
  If :horizontal is indicated, the weave will pass under horizontally.
  If the forests already have edges which would violate the requested weave,
  the original forests will be return unchanged."
  ([forests pos] (weave forests pos :vertical))
  ([forests [x y :as pos] dir]
   (let [middle (graph/find-forest forests pos)
         n-pos [x (dec y)]
         north (graph/find-forest forests n-pos)
         s-pos [x (inc y)]
         south (graph/find-forest forests s-pos)
         e-pos [(inc x) y]
         east (graph/find-forest forests e-pos)
         w-pos [(dec x) y]
         west (graph/find-forest forests w-pos)]
     (if (can-weave? north south east west middle dir)
       (if (= dir :horizontal)
         (let [vertical (graph/merge-forests
                          (graph/merge-forests north middle [n-pos pos])
                          south [pos s-pos])
               horizontal (graph/merge-forests east west [w-pos e-pos])]
           (-> forests
               (disj north south east west middle)
               (conj vertical)
               (conj horizontal)))
         ; (= dir :vertical)
         (let [horizontal (graph/merge-forests
                            (graph/merge-forests west middle [w-pos pos])
                            east [pos e-pos])
               vertical (graph/merge-forests north south [n-pos s-pos])]
           (-> forests
               (disj north south east west middle)
               (conj horizontal)
               (conj vertical))))
       ; ineligible
       forests))))
