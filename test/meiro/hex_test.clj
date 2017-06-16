(ns meiro.hex-test
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer :all]
            [meiro.core :as m]
            [meiro.hex :refer :all]
            [meiro.backtracker :as backtracker]))


(deftest neighbors-test
  (testing "First row behavior."
    (is (= [[1 0] [0 1]]
           (neighbors (m/init 3 3) [0 0])))
    (is (= [[1 1] [0 0] [0 2] [1 0] [1 2]]
           (neighbors (m/init 3 3) [0 1])))
    (is (= [[1 2] [0 1]]
           (neighbors (m/init 3 3) [0 2])))
    (is (= [[1 2] [0 1] [0 3]]
           (neighbors (m/init 4 4) [0 2])))
    (is (= [[1 3] [0 2] [1 2]]
           (neighbors (m/init 4 4) [0 3]))))
  (testing "Middle row behavior."
    (is (= [[0 0] [2 0] [0 1] [1 1]]
           (neighbors (m/init 3 3) [1 0])))
    (is (= [[0 1] [2 1] [1 0] [1 2] [2 0] [2 2]]
           (neighbors (m/init 3 3) [1 1])))
    (is (= [[0 2] [2 2] [0 1] [1 1]]
           (neighbors (m/init 3 3) [1 2])))
    (is (= [[0 2] [2 2] [0 1] [0 3] [1 1] [1 3]]
           (neighbors (m/init 4 4) [1 2])))
    (is (= [[0 3] [2 3] [1 2] [2 2]]
           (neighbors (m/init 4 4) [1 3]))))
  (testing "Last row behavior."
    (is (= [[2 0] [2 1] [3 1]]
           (neighbors (m/init 4 4) [3 0])))
    (is (= [[2 1] [3 0] [3 2]]
           (neighbors (m/init 4 4) [3 1])))
    (is (= [[2 2] [2 1] [2 3] [3 1] [3 3]]
           (neighbors (m/init 4 4) [3 2])))
    (is (= [[2 3] [3 2]]
           (neighbors (m/init 4 4) [3 3])))
    (is (= [[3 4] [3 3] [4 3]]
           (neighbors (m/init 5 5) [4 4])))))


(deftest direction-test
  (testing "North-south."
    (is (= :north (direction [2 3] [1 3])))
    (is (= :south (direction [1 5] [2 5]))))
  (testing "Northwest-southeast."
    (is (= :northwest (direction [1 1] [1 0])))
    (is (= :southeast (direction [1 0] [1 1])))
    (is (= :northwest (direction [1 2] [0 1])))
    (is (= :southeast (direction [0 1] [1 2]))))
  (testing "northeast-southwest."
    (is (= :northeast (direction [1 1] [1 2])))
    (is (= :southwest (direction [1 2] [1 1])))
    (is (= :northeast (direction [2 0] [1 1])))
    (is (= :southwest (direction [1 1] [2 0])))))


(deftest create-test
  (testing "Ensure all cells are linked."
    (is (every?
          #(not-any? empty? %)
          (backtracker/create (m/init 10 10) [0 0] empty-neighbors link)))))
