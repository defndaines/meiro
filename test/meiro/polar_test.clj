(ns meiro.polar-test
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer :all]
            [meiro.polar :refer :all]
            [meiro.core :as m]))


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
