(ns meiro.grid-3d-test
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer :all]
            [meiro.grid-3d :refer :all]))

;;; Position Functions

(deftest adjacent-test
  (testing "true if cells are adjacent."
    (is (adjacent? [0 0 0] [0 0 1]))
    (is (adjacent? [0 0 0] [0 1 0]))
    (is (adjacent? [0 0 0] [1 0 0]))
    (is (not (adjacent? [0 0 0] [0 0 2])))
    (is (not (adjacent? [0 0 0] [0 2 0])))
    (is (not (adjacent? [0 0 0] [2 0 0])))
    (is (not (adjacent? [0 0 0] [0 1 1])))
    (is (not (adjacent? [0 0 0] [1 0 1])))
    (is (not (adjacent? [0 0 0] [1 1 0])))))


(deftest direction-test
  (testing "Cardinal directions."
    (is (= :up (direction [2 2 3] [1 2 3])))
    (is (= :down (direction [2 2 3] [3 2 3])))
    (is (= :north (direction [2 2 3] [2 1 3])))
    (is (= :south (direction [2 3 1] [2 4 1])))
    (is (= :east (direction [2 5 1] [2 5 2])))
    (is (= :west (direction [2 4 3] [2 4 2]))))
  (testing "Not adjacent."
    (is (nil? (direction [0 0 0] [0 0 0])))
    (is (nil? (direction [0 0 0] [2 0 0])))
    (is (nil? (direction [0 0 0] [0 2 0])))
    (is (nil? (direction [0 0 0] [0 0 2])))))


(deftest init-test
  (testing "First levels, then rows, each row contains columns."
    (let [grid (init 4 5 3)]
      (is (= [[[[] [] []] [[] [] []] [[] [] []] [[] [] []] [[] [] []]]
              [[[] [] []] [[] [] []] [[] [] []] [[] [] []] [[] [] []]]
              [[[] [] []] [[] [] []] [[] [] []] [[] [] []] [[] [] []]]
              [[[] [] []] [[] [] []] [[] [] []] [[] [] []] [[] [] []]]]
             grid))
      (is (= 4 (count grid)))
      (is (= 5 (count (first grid))))
      (is (= 3 (count (first (first grid))))))))


(deftest in?-test
  (testing "Checking whether positions are in a grid."
    (let [grid (init 4 5 3)]
      (is (in? grid [0 0 0]))
      (is (in? grid [3 4 2]))
      (is (not (in? grid [0 0 -1])))
      (is (not (in? grid [0 -1 0])))
      (is (not (in? grid [-1 0 0])))
      (is (not (in? grid [3 4 3])))
      (is (not (in? grid [3 5 2])))
      (is (not (in? grid [4 4 2]))))))


(deftest neighbors-test
  (testing "Get all neighbors for a position."
    (let [grid (init 3 4 3)]
      (is (= [[0 1 0] [1 0 0] [0 0 1]]
             (neighbors grid [0 0 0])))
      (is (= [[2 3 1] [1 3 2] [2 2 2]]
             (neighbors grid [2 3 2])))
      (is (= [[1 3 1] [1 1 1] [2 2 1] [1 2 0] [1 2 2] [0 2 1]]
             (neighbors grid [1 2 1]))))))
