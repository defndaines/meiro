(ns meiro.backtracker
  "The Recursive Backtracker algorithm uses a biased random walk to generate a
  maze. While there are unvisited cells, it chooses one at random, but when it
  encounters a dead end, it backtracks on its path until it finds a cell with
  unvisited neighbors and walks, repeating until all cells have been linked."
  (:require [meiro.core :as m]
            [clojure.spec.alpha :as spec]))


(spec/fdef create
  :args (spec/alt
          :1-arg (spec/cat :grid :meiro.core/grid)
          :2-args (spec/cat :grid :meiro.core/grid :pos :meiro.core/pos)
          :4-args (spec/cat
                    :grid :meiro.core/grid
                    :pos :meiro.core/pos
                    :neighbor-fn ifn?
                    :link-fn ifn?)
          :5-args (spec/cat
                    :grid :meiro.core/grid
                    :pos :meiro.core/pos
                    :neighbor-fn ifn?
                    :link-fn ifn?
                    :select-fn ifn?))
  :ret :meiro.core/maze)
(defn create
  "Create a random maze using the Recursive Backtracker algorithm.
  If a `pos` is passed, then the random walk will begin at that position.
  `neighbor-fn` and `link-fn` allow for alternative (e.g., polar) mazes
  to use the algorithm. A `select-fn` allows for selecting which unvisited
  neighbor to visit."
  ([grid] (create grid (m/random-pos grid)))
  ([grid pos] (create grid pos m/neighbors m/link))
  ([grid pos neighbor-fn link-fn]
   (create grid pos neighbor-fn link-fn rand-nth))
  ([grid pos neighbor-fn link-fn select-fn]
   (loop [maze grid
          stack (list pos)]
     (if (seq stack)
       (let [pos (first stack)
             unvisited (m/empty-neighbors maze neighbor-fn pos)]
         (if (seq unvisited)
           (let [neighbor (select-fn unvisited)]
             (recur
               (link-fn maze pos neighbor)
               (conj stack neighbor)))
           (recur maze (rest stack))))
       maze))))
