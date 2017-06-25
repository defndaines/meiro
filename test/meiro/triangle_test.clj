(ns meiro.triangle-test
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer :all]
            [meiro.core :as m]
            [meiro.triangle :refer :all]
            [meiro.backtracker :as backtracker]))


(deftest neighbors-test
  (testing "Top row behavior."
    (is (= [[0 1] [1 0]]
           (neighbors (m/init 3 3) [0 0])))
    (is (= [[0 0] [0 2]]
           (neighbors (m/init 3 3) [0 1])))
    (is (= [[0 1] [1 2]]
           (neighbors (m/init 3 3) [0 2])))
    (is (= [[0 1] [0 3] [1 2]]
           (neighbors (m/init 4 4) [0 2])))
    (is (= [[0 2]]
           (neighbors (m/init 4 4) [0 3]))))
  (testing "Odd row behavior."
    (is (= [[1 1] [0 0]]
           (neighbors (m/init 3 3) [1 0])))
    (is (= [[1 0] [1 2] [2 1]]
           (neighbors (m/init 3 3) [1 1])))
    (is (= [[1 1] [0 2]]
           (neighbors (m/init 3 3) [1 2])))
    (is (= [[1 1] [1 3] [0 2]]
           (neighbors (m/init 4 4) [1 2])))
    (is (= [[1 2] [2 3]]
           (neighbors (m/init 4 4) [1 3]))))
  (testing "Even row behavior."
    (is (= [[2 1] [3 0]]
           (neighbors (m/init 4 4) [2 0])))
    (is (= [[2 0] [2 2] [1 1]]
           (neighbors (m/init 4 4) [2 1])))
    (is (= [[2 1] [3 2]]
           (neighbors (m/init 4 3) [2 2])))
    (is (= [[2 1] [2 3] [3 2]]
           (neighbors (m/init 4 4) [2 2])))
    (is (= [[2 2] [1 3]]
           (neighbors (m/init 4 4) [2 3])))))


(deftest create-test
  (testing "Ensure all cells are linked."
    (is (every?
          #(not-any? empty? %)
          (backtracker/create (m/init 10 10) [0 0] neighbors m/link)))))
