(ns meiro.eller
  "Eller's algorithm generates a forest row-by-row, combining all the forests at
  the end into a single result. It behaves like Sidewinder in that it can merge
  cells left to right, but then only one passage will link south."
  (:require [meiro.core :as m]
            [meiro.graph :as graph]
            [clojure.spec.alpha :as spec]))


(def ^:private horizontal-weight
  "Weight used to decide whether to link horizontally in a row.
  The higher the weight, the greater the chance that cells will be linked in a
  row. 1.0 will cause all cells in a row to be linked."
  0.5)

(def ^:private vertical-weight
  "Weight used to decide whether to link vertically from a row.
  The higher the weight, the greater the chance that cells will be linked to
  the row beneath. 1.0 will cause all cells in a row to be linked."
  0.5)


(spec/fdef for-forests
  :args (spec/cat :forests :meiro.graph/forest
                  :row nat-int?
                  :width :meiro.graph/width
                  :height :meiro.graph/height)
  :ret :meiro.graph/forest)
(defn for-forests
  "Get the forests for a row. If there is no forest yet for a node in the row,
  a new forest will be generated."
  [forests row width height]
  (reduce
    (fn [acc pos]
      (if-let [forest (graph/find-forest acc pos)] ;; TODO Unused!?!
        acc
        (conj acc {:width width :height height :nodes #{pos} :edges []})))
    forests
    (for [col (range width)] [col row])))


(spec/fdef neighboring-forests
  :args (spec/cat :forests :meiro.graph/forest :node :meiro.graph/node)
  :ret :meiro.graph/forest)
(defn- neighboring-forests
  "Get all forests with nodes adjancent to the provided node."
  [forests node]
  (filter
    (fn [f] (some #(m/adjacent? node %) (:nodes f)))
    forests))


(spec/fdef merge-all
  :args (spec/cat :forests :meiro.graph/forest)
  :ret :meiro.graph/forest)
(defn merge-all
  "Merge all forests by finding an adjacent node in another forest to link."
  [forests]
  (loop [[forest & tail] forests]
    (if (seq tail)
      (let [node (first (shuffle (:nodes forest)))
            neighbors (neighboring-forests tail node)]
        (if (seq neighbors)
          (let [neighbor (first (shuffle neighbors))
                adj (filter (fn [n] (m/adjacent? node n)) (:nodes neighbor))
                edge (vec (sort [node (rand-nth adj)]))]
            (recur
              (conj
                (remove #(= % neighbor) tail)
                (graph/merge-forests forest neighbor edge))))
          (recur (conj tail forest))))
      forest)))


(spec/fdef link-horizontal
  :args (spec/alt
          :2-args (spec/cat :forests :meiro.graph/forest :row nat-int?)
          :3-args (spec/cat :forests :meiro.graph/forest
                            :row nat-int?
                            :weight :meiro.core/rate))
  :ret :meiro.graph/forest)
(defn link-horizontal
  "Randomly link some cells in a row."
  ([forests row] (link-horizontal forests row horizontal-weight))
  ([forests row weight]
   (reduce
     (fn [acc [x y :as pos]]
       (let [forest (graph/find-forest acc pos)]
         (if (< (rand) weight)
           (let [pos-right [(inc x) y]
                 neighbor (graph/find-forest acc pos-right)]
             (if (map? neighbor)
               (-> acc
                   (disj forest neighbor)
                   (conj (graph/merge-forests forest neighbor [pos pos-right])))
               acc))
           acc)))
     forests
     (for [x (range (:width (first forests)))] [x row]))))


(spec/fdef link-vertical
  :args (spec/alt
          :2-args (spec/cat :forests :meiro.graph/forest :row nat-int?)
          :3-args (spec/cat :forests :meiro.graph/forest
                            :row nat-int?
                            :weight :meiro.core/rate))
  :ret :meiro.graph/forest)
(defn link-vertical
  "Randomly link some cells in a row to the next row."
  ;; TODO Currently only links once per corridor. Refactor to change this bias?
  ([forests row] (link-vertical forests row vertical-weight))
  ([forests row weight]
   (let [next-row (inc row)
         height (:height (first forests))]
     (if (= next-row height)
       forests
       (reduce
         (fn [acc forest]
           (let [corridor (filter (fn [[_ y]] (= row y)) (:nodes forest))]
             (if (seq corridor)
               (if (< (rand) weight)
                 (let [[x _ :as pos] (rand-nth corridor)
                       pos-below [x next-row]]
                   (-> acc
                       (disj forest)
                       (conj (graph/merge-forests
                               forest
                               {:nodes #{pos-below}}
                               [pos pos-below]))))
                 acc)
               acc)))
         forests
         forests)))))


(spec/fdef create
  :args (spec/cat :width :meiro.graph/width :height :meiro.graph/height)
  :ret :meiro.graph/forest)
(defn create
  "Create a maze using Eller's algorithm. Returns a forest."
  [width height]
  (loop [row 0
         forests #{}]
    (if (= row height)
      (merge-all forests)
      (recur
        (inc row)
        (-> forests
            (for-forests row width height)
            (link-horizontal row)
            (link-vertical row))))))
