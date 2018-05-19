(ns meiro.sidewinder-test
  (:require [clojure.test :refer [deftest testing is]]
            [meiro.core :as meiro]
            [meiro.sidewinder :as sidewinder]))


(deftest western-path-test
  (let [maze [[[:south] [:south] [:east] [:west :east]
               [:west :south] [:south] [:east]
               [:west :south]]
              [[:north :east] [:north :west :south] [:east] [:west :east]
               [:north :west :east] [:north :west :south] [:south]
               [:north :south]]
              [[:east] [:north :west :east] [:west :east] [:west :east]
               [:west :east] [:north :west :east] [:north :west :east]
               [:north :west]]]]
    (testing "No linked cell to the west"
      (is (= '([0 0]) (#'meiro.sidewinder/path-west maze [0 0]))))
    (testing "One linked cell to the west"
      (is (= '([0 3] [0 2]) (#'meiro.sidewinder/path-west maze [0 3]))))
    (testing "Multiple linked cells to the west"
      (is (= '([1 5] [1 4] [1 3] [1 2])
             (#'meiro.sidewinder/path-west maze [1 5]))))))


(deftest create-test
  (testing "Ensure all cells are linked."
    (is (every? #(not-any? empty? %) (sidewinder/create (meiro/init 10 12))))))


(deftest corridor-test
  (testing "Get corridor paths."
    (let [row [[:east] [] [:east] [:east] []]]
      (is (= [0]
             (#'meiro.sidewinder/corridor row 0)))
      (is (= [1 0]
             (#'meiro.sidewinder/corridor row 1)))
      (is (= [2]
             (#'meiro.sidewinder/corridor row 2)))
      (is (= [3 2]
             (#'meiro.sidewinder/corridor row 3)))
      (is (= [4 3 2]
             (#'meiro.sidewinder/corridor row 4))))))


(deftest create-row-test
  (testing "Weights used to decide direction."
    (is (= [[:east] [:east] [:east] [:south]]
           (#'meiro.sidewinder/create-row 4 {:south 0 :east 1})))
    (is (= [[:south] [:south] [:south] [:south]]
           (#'meiro.sidewinder/create-row 4 {:south 1 :east 0})))))


(deftest last-row-test
  (testing "Last row can only link to itself."
    (is (= [[:east] [:east] [:east] [:west]]
           (sidewinder/last-row 4)))))


(deftest create-lazy-test
  (testing "Build a maze using infinite approach."
    (let [maze (sidewinder/create-lazy 8)]
      (is (= 8
             (count (first (drop 25 maze))))))))
