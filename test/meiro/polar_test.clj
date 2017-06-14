(ns meiro.polar-test
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer :all]
            [meiro.polar :refer :all]
            [meiro.backtracker :as backtracker]))


(deftest init-test
  (testing "Number of cells per row increase."
    (is (= [[[]]] (init 1)))
    (is (= [[[]] [[] [] [] [] [] []]] (init 2)))
    (is (= [[[]]
            [[] [] [] [] [] []]
            [[] [] [] [] [] [] [] [] [] [] [] []]]
           (init 3)))
    (is (= 192
           (count (last (init 36))))))

  (testing "Create with value other than []."
    (is (= [[nil] [nil nil nil nil nil nil]] (init 2 nil)))
    (is (= [[0]
            [0 0 0 0 0 0]
            [0 0 0 0 0 0 0 0 0 0 0 0]]
           (init 3 0)))))


(deftest neighbors-test
  (testing "Row 0 behavior."
    (is (= [[1 0] [1 1] [1 2] [1 3] [1 4] [1 5]]
           (neighbors (init 2) [0 0]))))
  (testing "Row 1 behavior."
    (is (= [[0 0] [1 5] [1 1] [2 0] [2 1]]
           (neighbors (init 3) [1 0]))))
  (testing "Inward decreases."
    (is (= [[1 3] [2 6] [2 8] [3 14] [3 15]]
           (neighbors (init 4) [2 7])))
    (is (= [[1 4] [2 7] [2 9] [3 16] [3 17]]
           (neighbors (init 4) [2 8])))
    (is (= [[1 4] [2 8] [2 10] [3 18] [3 19]]
           (neighbors (init 4) [2 9]))))
  (testing "Outward increases."
    (is (= [[4 22] [5 21] [5 23] [6 44] [6 45]]
           (neighbors (init 7) [5 22])))
    (is (= [[4 23] [5 22] [5 0] [6 46] [6 47]]
           (neighbors (init 7) [5 23]))))
  (testing "No inward or outward change."
    (is (= [[7 13] [8 12] [8 14] [9 13]]
           (neighbors (init 10) [8 13]))))
  (testing "Last row."
    (is (= [[8 13] [9 12] [9 14]]
           (neighbors (init 10) [9 13])))))


(deftest direction-test
  (testing "Cardinal directions."
    (is (= :inward (direction [2 3] [1 3])))
    (is (= :inward (direction [1 5] [0 0])))
    (is (= :inward (direction [1 0] [0 0])))
    (is (= :clockwise (direction [5 1] [5 2])))
    (is (= :counter-clockwise (direction [4 3] [4 2]))))
  (testing "Wrap around the grid."
    (is (= :clockwise (direction [1 5] [1 0])))
    (is (= :counter-clockwise (direction [1 0] [1 5]))))
  (testing "South cells just return coordinates."
    (is (= [4 1] (direction [3 1] [4 1])))))


(deftest link-test
  (testing "Cells link."
    (let [center [0 0]
          south [1 3]
          maze (link (init 3) center south)]
      (is (= [south] (get-in maze center)))
      (is (= [:inward] (get-in maze south))))))


(deftest create-test
  (testing "Ensure all cells are linked."
    (is (every?
          #(not-any? empty? %)
          (backtracker/create (init 10) [0 0] empty-neighbors link)))))
