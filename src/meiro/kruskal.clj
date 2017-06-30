(ns meiro.kruskal
  "Kruskal's algorithm uses a minimum spanning tree to connect cells in a maze.
  Although spanning trees typically have weights to edges to resolve paths,
  using a random sort to the edges can achieve the effect we need to create a
  maze.
  This algorithm is not designed to work with the other approaches.
  Instead of using a row-column arrangement, this algorithm uses x y
  coordinates aligned with the needs of a renderer.")


(defn- all-edges
  "Get all edges in a grid."
  [width height]
  (concat
    (for [x (range (dec width)) y (range height)]
      [[x y] [(inc x) y]])
    (for [x (range width) y (range (dec height))]
      [[x y] [x (inc y)]])))


(defn- init-forests
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
  [width height]
  (loop [forests (init-forests width height)
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
      (:edges (first forests)))))
