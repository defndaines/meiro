(ns meiro.growing-tree-test
  (:require [clojure.test :refer :all]
            [meiro.growing-tree :refer :all]
            [meiro.prim]
            [meiro.graph :as graph]))


(deftest newer-pos-test
  (testing "Get the newer position from an edge."
    (is (nil? (#'meiro.growing-tree/outside-forest #{[0 0] [0 1]}
                                                   [[0 0] [0 1]])))
    (is (= [0 1]
           (#'meiro.growing-tree/outside-forest #{[0 0]}
                                                [[0 0] [0 1]])))))


(deftest recreate-prims-algorithm-test
  (testing "Creating a maze using Prim's Algorithm."
    (is (= (dec (* 8 12))
           (count (create 8 12
                          (java.util.PriorityQueue.)
                          #'meiro.prim/poll
                          #'meiro.prim/to-active!)))))
  (testing "Ensure all cells are linked."
    (is (every?
          #(not-any? empty? %)
          (graph/edges-to-grid
            (create 10 12
                    (java.util.PriorityQueue.)
                    #'meiro.prim/poll
                    #'meiro.prim/to-active!)
            10 12)))))


(deftest recreate-recursive-backtracker-test
  (testing "Creating a maze using recursive backtracker."
    (is (every?
          #(not-any? empty? %)
          (graph/edges-to-grid
            (create 18 10
                    '()
                    (fn [q] [(first q) (rest q)])
                    (fn [new-edges queue remaining-edges]
                      (reduce
                        (fn [[q es] e]
                          (let [remaining (disj es e)]
                            (if (= es remaining)
                              [q es]
                              [(conj q e)
                               remaining])))
                        [queue remaining-edges]
                        (shuffle new-edges))))
            18 10)))))
