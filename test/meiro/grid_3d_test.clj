(ns meiro.grid-3d-test
  (:require [clojure.test :refer [deftest testing is]]
            [meiro.grid-3d :as grid-3d]))

;;; Position Functions

(deftest adjacent-test
  (testing "true if cells are adjacent."
    (is (grid-3d/adjacent? [0 0 0] [0 0 1]))
    (is (grid-3d/adjacent? [0 0 0] [0 1 0]))
    (is (grid-3d/adjacent? [0 0 0] [1 0 0]))
    (is (not (grid-3d/adjacent? [0 0 0] [0 0 2])))
    (is (not (grid-3d/adjacent? [0 0 0] [0 2 0])))
    (is (not (grid-3d/adjacent? [0 0 0] [2 0 0])))
    (is (not (grid-3d/adjacent? [0 0 0] [0 1 1])))
    (is (not (grid-3d/adjacent? [0 0 0] [1 0 1])))
    (is (not (grid-3d/adjacent? [0 0 0] [1 1 0])))))


(deftest direction-test
  (testing "Cardinal directions."
    (is (= :down (grid-3d/direction [2 2 3] [1 2 3])))
    (is (= :up (grid-3d/direction [2 2 3] [3 2 3])))
    (is (= :north (grid-3d/direction [2 2 3] [2 1 3])))
    (is (= :south (grid-3d/direction [2 3 1] [2 4 1])))
    (is (= :east (grid-3d/direction [2 5 1] [2 5 2])))
    (is (= :west (grid-3d/direction [2 4 3] [2 4 2]))))
  (testing "Not adjacent."
    (is (nil? (grid-3d/direction [0 0 0] [0 0 0])))
    (is (nil? (grid-3d/direction [0 0 0] [2 0 0])))
    (is (nil? (grid-3d/direction [0 0 0] [0 2 0])))
    (is (nil? (grid-3d/direction [0 0 0] [0 0 2])))))


(deftest init-test
  (testing "First levels, then rows, each row contains columns."
    (let [grid (grid-3d/init 4 5 3)]
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
    (let [grid (grid-3d/init 4 5 3)]
      (is (grid-3d/in? grid [0 0 0]))
      (is (grid-3d/in? grid [3 4 2]))
      (is (not (grid-3d/in? grid [0 0 -1])))
      (is (not (grid-3d/in? grid [0 -1 0])))
      (is (not (grid-3d/in? grid [-1 0 0])))
      (is (not (grid-3d/in? grid [3 4 3])))
      (is (not (grid-3d/in? grid [3 5 2])))
      (is (not (grid-3d/in? grid [4 4 2]))))))


(deftest neighbors-test
  (testing "Get all neighbors for a position."
    (let [grid (grid-3d/init 3 4 3)]
      (is (= [[0 1 0] [1 0 0] [0 0 1]]
             (grid-3d/neighbors grid [0 0 0])))
      (is (= [[2 3 1] [1 3 2] [2 2 2]]
             (grid-3d/neighbors grid [2 3 2])))
      (is (= [[1 3 1] [1 1 1] [2 2 1] [1 2 0] [1 2 2] [0 2 1]]
             (grid-3d/neighbors grid [1 2 1]))))))


(deftest random-pos-test
  (testing "Random position is from within the grid."
    (let [grid (grid-3d/init 6 5 4)]
      (is (grid-3d/in? grid (grid-3d/random-pos grid)))))
  (testing "Ignore masked cells."
    (let [base (grid-3d/init 2 2 2)
          grid (reduce (fn [acc e] (update-in acc e conj :mask)) base
                       (for [z [0 1] y [0 1] x [0 1]
                             :when (not= 0 z y x)]
                         [z y x]))]
      (is (= [0 0 0] (grid-3d/random-pos grid))))))
