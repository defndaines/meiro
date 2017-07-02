(ns meiro.kruskal
  "Kruskal's algorithm uses a minimum spanning tree to connect cells in a maze.
  Although spanning trees typically have weights to edges to resolve paths,
  using a random sort to the edges can achieve the effect we need to create a
  maze.
  This algorithm is not designed to work with the other approaches.
  Instead of using a row-column arrangement, this algorithm uses x y
  coordinates aligned with the needs of a renderer."
  (:require [meiro.core :as m]))


(defn- all-edges
  "Get all edges in a grid."
  [width height]
  (concat
    (for [x (range (dec width)) y (range height)]
      [[x y] [(inc x) y]])
    (for [x (range width) y (range (dec height))]
      [[x y] [x (inc y)]])))


(defn init-forests
  "Get all the nodes in a grid and put them into forest maps."
  [width height]
  (reduce
    (fn [acc e] (conj acc {:nodes #{e} :edges []}))
    #{}
    (for [x (range width) y (range height)] [x y])))


(defn- find-forest
  "Get the forest containing the position."
  [forests pos]
  (first
    (filter
      (fn [f] (contains? (:nodes f) pos))
      forests)))


(defn- merge-forests
  "Merge two forests into forest set."
  [f-1 f-2 edge]
  (let [{ns-1 :nodes es-1 :edges} f-1
        {ns-2 :nodes es-2 :edges} f-2]
    {:nodes (clojure.set/union ns-1 ns-2)
     :edges (concat es-1 es-2 [edge])}))


(defn create
  "Create a maze with the provided dimensions."
  ([width height] (create width height (init-forests width height)))
  ([width height forests]
   (loop [forests forests
          edges (shuffle (all-edges width height))]
     (if (> (count forests) 1)
       (let [[pos-1 pos-2 :as edge] (first edges)
             f-1 (find-forest forests pos-1)
             f-2 (find-forest forests pos-2)]
         (recur
           (if (= f-1 f-2)
             forests
             (let [merged (merge-forests f-1 f-2 edge)]
               (-> forests
                   (disj f-1)
                   (disj f-2)
                   (conj merged))))
           (rest edges)))
       (:edges (first forests))))))


(defn edges-to-grid
  "Convert a set of edges to the standard maze format used in the PNG
  functions."
  [edges width height]
  (reduce
    (fn [maze [[x y] [x' y']]]
      (m/link maze [y x] [y' x']))
    (m/init height width)
    edges))


(defn- eligible-to-weave?
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


(defn- disj-all
  "Remove the forests."
  [forests & sets]
  (reduce
    (fn [acc e] (disj acc e))
    forests
    sets))


(defn weave
  "Add a weave to the forests centered on the provided `pos`.
  A direction can also be passed, either `:horizontal` or `:vertical`.
  If :horizontal is indicated, the weave will pass under horizontally.
  If the forests already have edges which would violate the requested weave,
  the original forests will be return unchanged."
  ([forests pos] (weave forests pos :vertical))
  ([forests [x y :as pos] dir]
   (let [middle (find-forest forests pos)
         n-pos [x (dec y)]
         north (find-forest forests n-pos)
         s-pos [x (inc y)]
         south (find-forest forests s-pos)
         e-pos [(inc x) y]
         east (find-forest forests e-pos)
         w-pos [(dec x) y]
         west (find-forest forests w-pos)]
     (if (eligible-to-weave? north south east west middle dir)
       (if (= dir :horizontal)
         (let [vertical (merge-forests
                          (merge-forests north middle [n-pos pos])
                          south [pos s-pos])
               horizontal (merge-forests east west [w-pos e-pos])]
           (-> forests
               (disj-all north south east west middle)
               (conj vertical)
               (conj horizontal)))
         ; (= dir :vertical)
         (let [horizontal (merge-forests
                            (merge-forests west middle [w-pos pos])
                            east [pos e-pos])
               vertical (merge-forests north south [n-pos s-pos])]
           (-> forests
               (disj-all north south east west middle)
               (conj horizontal)
               (conj vertical))))
       ; ineligible
       forests))))

;; TODO Keep height and width in forests map
