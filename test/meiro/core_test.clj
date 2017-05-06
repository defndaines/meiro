(ns meiro.core-test
  (:require [clojure.test :refer :all]
            [meiro.core :refer :all]))

(deftest init-test
  (testing "First level is row, each row contains columns."
    (is (= 5 (count (init 5 3))))
    (is (every? #(= 4 (count %)) (init 5 4))))
  (testing "Create with value other than []."
    (is (= [[nil nil] [nil nil]] (init 2 2 nil)))
    (is (= [[0] [0] [0]] (init 3 1 0)))))

(deftest direction-test
  (testing "Cardinal directions."
    (is (= :north (direction [2 3] [1 3])))
    (is (= :south (direction [3 1] [4 1])))
    (is (= :east (direction [5 1] [5 2])))
    (is (= :west (direction [4 3] [4 2]))))
  (testing "Not adjacent."
    (is (nil? (direction [0 0] [0 0])))
    (is (nil? (direction [0 0] [2 0])))
    (is (nil? (direction [0 0] [0 2])))))

(deftest adjacent-test
  (testing "true if cells are adjacent."
    (is (adjacent? (init 5 5) [0 0] [0 1]))
    (is (adjacent? (init 5 5) [0 0] [1 0]))
    (is (not (adjacent? (init 5 5) [0 0] [0 2])))
    (is (not (adjacent? (init 5 5) [0 0] [2 0]))))
  (testing "false if cell outside maze."
    (is (not (adjacent? (init 5 5) [0 0] [-1 0])))
    (is (not (adjacent? (init 5 5) [0 0] [0 -1])))
    (is (not (adjacent? (init 5 5) [4 0] [5 0])))
    (is (not (adjacent? (init 5 5) [0 4] [0 5])))))

(deftest link-test
  (testing "Adjacent cells linked by opposite directions."
    (let [above [2 2]
          below [3 2]
          m (link (init 6 4) below above)]
      (is (some (comp = :north) (get-in m below)))
      (is (some (comp = :south) (get-in m above))))
    (let [left [1 2]
          right [1 3]
          m (link (init 6 4) left right)]
      (is (some (comp = :east) (get-in m left)))
      (is (some (comp = :west) (get-in m right)))))
  (testing "Non-adjacent cells do not link."
    (let [m (init 5 5)]
      (is (= m (link m [0 0] [1 1]))))))

(deftest neighbors-test
  (testing "Get neighbors to a cell in a maze."
    (is (= #{[0 1] [1 0] [1 2] [2 1]} (set (neighbors (init 3 3) [1 1]))))
    (is (= #{[1 1] [0 0] [2 0]} (set (neighbors (init 3 3) [1 0]))))
    (is (= #{[0 0] [0 2] [1 1]} (set (neighbors (init 3 3) [0 1]))))
    (is (= #{[0 1] [1 0]} (set (neighbors (init 3 3) [0 0]))))
    (is (= #{[0 1] [1 2]} (set (neighbors (init 3 3) [0 2]))))
    (is (= #{[1 0] [2 1]} (set (neighbors (init 3 3) [2 0]))))
    (is (= #{[2 1] [1 2]} (set (neighbors (init 3 3) [2 2]))))))

(deftest cell-direction-test
  (testing "Methods for getting the cell in a given direction"
    (is (= [0 1] (north [1 1])))
    (is (= [2 1] (south [1 1])))
    (is (= [1 2] (east [1 1])))
    (is (= [1 0] (west [1 1])))))

(deftest cell-to-test
  (testing "Methods for getting the cell in a given direction"
    (is (= [0 1] (cell-to :north [1 1])))
    (is (= [2 1] (cell-to :south [1 1])))
    (is (= [1 2] (cell-to :east [1 1])))
    (is (= [1 0] (cell-to :west [1 1])))))

(deftest western-cells-test
  (let [maze [[[:south] [:south] [:east] [:west :east] [:west :south] [:south] [:east] [:west :south]]
              [[:north :east] [:north :west :south] [:east] [:west :east] [:north :west :east] [:north :west :south] [:south] [:north :south]]
              [[:east] [:north :west :east] [:west :east] [:west :east] [:west :east] [:north :west :east] [:north :west :east] [:north :west]]]]
    (testing "No linked cell to the west"
      (is (= '([0 0]) (cells-west maze [0 0]))))
    (testing "One linked cell to the west"
      (is (= '([0 3] [0 2]) (cells-west maze [0 3]))))
    (testing "Multiple linked cells to the west"
      (is (= '([1 5] [1 4] [1 3] [1 2]) (cells-west maze [1 5]))))))
