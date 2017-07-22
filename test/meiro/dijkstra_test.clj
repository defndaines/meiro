(ns meiro.dijkstra-test
  (:require [clojure.test :refer :all]
            [meiro.core :refer :all]
            [meiro.dijkstra :refer :all]
            [meiro.sidewinder :as sw]))


(deftest empty-neighbor-test
  (testing "When the grid is empty."
    (is (= [[0 1] [2 1] [1 2] [1 0]]
           (#'meiro.dijkstra/empty-neighbors
             (init 3 3 nil) [1 1] [:north :south :east :west])))
   (is (= [[2 1] [1 2]]
           (#'meiro.dijkstra/empty-neighbors
             (init 3 3 nil) [1 1] [:south :east]))))
  (testing "When cells have been populated already"
    (let [grid (assoc-in (init 3 3 nil) [2 1] 1)]
      (is (= [[1 2]]
             (#'meiro.dijkstra/empty-neighbors grid [1 1] [:south :east])))
      (is (= [[1 0]]
             (#'meiro.dijkstra/empty-neighbors grid [2 0] [:north :east]))))))


(deftest calculate-distances-test
  (testing "Distance calculations."
    (let [maze [[[:east :south] [:west :east] [:west]]
                [[:north :south] [:east] [:west :south]]
                [[:north :east] [:west :east] [:north :west]]]]
      (is (= [[0 1 2] [1 6 5] [2 3 4]]
             (distances maze)))
      (is (= [[6 7 8] [5 0 1] [4 3 2]]
             (distances maze [1 1]))))))


(deftest distances-by-breadth-test
  (testing "Calculating distances by breadth-first search."
    (let [maze [[[:east :south] [:west :east] [:west]]
                [[:north :south] [:east] [:west :south]]
                [[:north :east] [:west :east] [:north :west]]]]
      (is (= [[0 1 2] [1 6 5] [2 3 4]]
             (distances-by-breadth maze)))
      (is (= [[6 7 8] [5 0 1] [4 3 2]]
             (distances-by-breadth maze [1 1])))))
  (testing "Calculate distances on a braided maze."
    (is (= [[0 3 4] [1 2 5] [2 3 4]]
           (distances-by-breadth
             [[[:south :south] [:east :south] [:south :west]]
              [[:east :north :south :north] [:north :west] [:south :north]]
              [[:north :east] [:east :west] [:west :north]]]))))
  (testing "Calculate distances on a maze with a room."
    (is (= [[2 1 2] [1 0 3] [2 1 4]]
           (distances-by-breadth
             [[[:east] [:south :east :west] [:south :west]]
              [[:south :east] [:north :south :west] [:north :south]]
              [[:north :east] [:north :west] [:north]]]
             [1 1])))))


(deftest solution-test
  (let [sol (solution (sw/create (init 15 20)) [0 0] [14 19])]
    (testing "Must be long enough to cross at least all columns and rows."
      (is (< 33 (count sol))))
    (testing "Starts with the provided 'start' cell."
      (is (= [0 0] (first sol))))
    (testing "Ends with the provided 'end' cell."
      (is (= [14 19] (last sol)))))
  (testing "Solution does not jump walls."
    ;; Without bounds checking solution can pass through [2 1] instead of [3 0].
    (let [maze [[[:east] [:west :east :south] [:west] [:south]
                 [:south]]
                [[:east :south] [:north :west] [:south] [:north :east]
                 [:north :west :south]]
                [[:north :east :south] [:west] [:north :east :south] [:west]
                 [:north :south]]
                [[:north :east] [:west :east] [:north :west :east] [:west :east]
                 [:north :west]]]]
      (is (= '([0 0] [0 1] [1 1] [1 0] [2 0] [3 0] [3 1] [3 2] [3 3] [3 4] [2 4]
               [1 4] [0 4])
             (solution maze [0 0] [0 4]))))))


(deftest farthest-test
  (let [maze [[[:east :south] [:west] [:south]]
              [[:north :east] [:west :east :south] [:north :west]]
              [[:east] [:north :west :east] [:west]]]]
    (testing "Finding the farthest point."
      (is (= [2 2] (farthest-pos maze)))
      (is (= [0 1] (farthest-pos maze [0 2])))
      (is (= [0 1] (farthest-pos maze [2 0]))))))


(deftest longest-path-test
  (let [maze [[[:east :south] [:west] [:south]]
              [[:north :east] [:west :east :south] [:north :west]]
              [[:east] [:north :west :east] [:west]]]]
    (testing "Finding the longest path."
      (is (= '([0 1] [0 0] [1 0] [1 1] [2 1] [2 2]) (longest-path maze))))))
