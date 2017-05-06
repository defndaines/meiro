(ns meiro.dijkstra-test
  (:require [clojure.test :refer :all]
            [meiro.core :refer :all]
            [meiro.dijkstra :refer :all]))

(deftest empty-neighbor-test
  (testing "When the grid is empty."
    (is (= [[0 1] [2 1] [1 2] [1 0]]
           (#'meiro.dijkstra/empty-neighbors (init 3 3 nil) [1 1] [:north :south :east :west])))
   (is (= [[2 1] [1 2]]
           (#'meiro.dijkstra/empty-neighbors (init 3 3 nil) [1 1] [:south :east]))))
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
