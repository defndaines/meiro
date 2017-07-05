(ns meiro.prim-test
  (:require [clojure.test :refer :all]
            [meiro.prim :refer :all]
            [meiro.graph :as graph]))


(deftest to-active-test
  (testing "Valid edges transfer from known edges into queue."
    ;; North-west corner
    (let [queue (java.util.PriorityQueue.)
          known-edges (set (graph/all-edges 3 4))
          [after-queue after-edges] (#'meiro.prim/to-active!
                                      (#'meiro.prim/pos-edges [0 0])
                                      queue
                                      known-edges)]
      (is (= 2 (.size after-queue)))
      (is (not (let [[_ edge] (.peek after-queue)]
                 (contains? after-edges edge))))
      (is (let [[_ edge] (.poll after-queue)]
            (or (= [[0 0] [0 1]] edge)
                (= [[0 0] [1 0]] edge)))))
    ;; South-east corner
    (let [queue (java.util.PriorityQueue.)
          known-edges (set (graph/all-edges 3 4))
          [after-queue after-edges] (#'meiro.prim/to-active!
                                      (#'meiro.prim/pos-edges [2 3])
                                      queue
                                      known-edges)]
      (is (= 2 (.size after-queue)))
      (is (not (let [[_ edge] (.peek after-queue)]
                 (contains? after-edges edge))))
      (is (let [[_ edge] (.poll after-queue)]
            (or (= [[1 3] [2 3]] edge)
                (= [[2 2] [2 3]] edge)))))
    ;; Middle
    (let [queue (java.util.PriorityQueue.)
          known-edges (set (graph/all-edges 3 4))
          [after-queue after-edges] (#'meiro.prim/to-active!
                                      (#'meiro.prim/pos-edges [1 2])
                                      queue
                                      known-edges)]
      (is (= 4 (.size after-queue)))
      (is (not (let [[_ edge] (.peek after-queue)]
                 (contains? after-edges edge))))
      (is (let [[_ edge] (.poll after-queue)]
            (or (= [[0 2] [1 2]] edge)
                (= [[1 1] [1 2]] edge)
                (= [[1 2] [2 2]] edge)
                (= [[1 2] [1 3]] edge)))))))


(deftest newer-pos-test
  (testing "Get the newer position from an edge."
    (is (nil? (#'meiro.prim/newer-pos #{[0 0] [0 1]}
                                      [[0 0] [0 1]])))
    (is (= [0 1]
           (#'meiro.prim/newer-pos #{[0 0]}
                                   [[0 0] [0 1]])))))


(deftest create-test
  (testing "Creating a maze using Prim's Algorithm."
    (is (= (dec (* 8 12))
           (count (:edges (create 8 12))))))
  (testing "Ensure all cells are linked."
    (is (every?
          #(not-any? empty? %)
          (graph/forest-to-maze (create 10 12))))))
