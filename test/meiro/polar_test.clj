(ns meiro.polar-test
  (:require [clojure.test :refer [deftest testing is]]
            [meiro.polar :as polar]
            [meiro.backtracker :as backtracker]))


(deftest init-test
  (testing "Number of cells per row increase."
    (is (= [[[]]] (polar/init 1)))
    (is (= [[[]] [[] [] [] [] [] []]] (polar/init 2)))
    (is (= [[[]]
            [[] [] [] [] [] []]
            [[] [] [] [] [] [] [] [] [] [] [] []]]
           (polar/init 3)))
    (is (= 192
           (count (last (polar/init 36))))))

  (testing "Create with value other than []."
    (is (= [[nil] [nil nil nil nil nil nil]] (polar/init 2 nil)))
    (is (= [[0]
            [0 0 0 0 0 0]
            [0 0 0 0 0 0 0 0 0 0 0 0]]
           (polar/init 3 0)))))


(deftest neighbors-test
  (testing "Row 0 behavior."
    (is (= [[1 0] [1 1] [1 2] [1 3] [1 4] [1 5]]
           (polar/neighbors (polar/init 2) [0 0]))))
  (testing "Row 1 behavior."
    (is (= [[0 0] [1 5] [1 1] [2 0] [2 1]]
           (polar/neighbors (polar/init 3) [1 0]))))
  (testing "Inward decreases."
    (is (= [[1 3] [2 6] [2 8] [3 14] [3 15]]
           (polar/neighbors (polar/init 4) [2 7])))
    (is (= [[1 4] [2 7] [2 9] [3 16] [3 17]]
           (polar/neighbors (polar/init 4) [2 8])))
    (is (= [[1 4] [2 8] [2 10] [3 18] [3 19]]
           (polar/neighbors (polar/init 4) [2 9]))))
  (testing "Outward increases."
    (is (= [[4 22] [5 21] [5 23] [6 44] [6 45]]
           (polar/neighbors (polar/init 7) [5 22])))
    (is (= [[4 23] [5 22] [5 0] [6 46] [6 47]]
           (polar/neighbors (polar/init 7) [5 23]))))
  (testing "No inward or outward change."
    (is (= [[7 13] [8 12] [8 14] [9 13]]
           (polar/neighbors (polar/init 10) [8 13]))))
  (testing "Last row."
    (is (= [[8 13] [9 12] [9 14]]
           (polar/neighbors (polar/init 10) [9 13])))))


(deftest direction-test
  (testing "Cardinal directions."
    (is (= :inward (polar/direction [2 3] [1 3])))
    (is (= :inward (polar/direction [1 5] [0 0])))
    (is (= :inward (polar/direction [1 0] [0 0])))
    (is (= :clockwise (polar/direction [5 1] [5 2])))
    (is (= :counter-clockwise (polar/direction [4 3] [4 2]))))
  (testing "Wrap around the grid."
    (is (= :clockwise (polar/direction [1 5] [1 0])))
    (is (= :counter-clockwise (polar/direction [1 0] [1 5]))))
  (testing "South cells just return coordinates."
    (is (= [4 1] (polar/direction [3 1] [4 1])))))


(deftest link-test
  (testing "Cells link."
    (let [center [0 0]
          south [1 3]
          maze (polar/link (polar/init 3) center south)]
      (is (= [south] (get-in maze center)))
      (is (= [:inward] (get-in maze south))))))


(deftest create-test
  (testing "Ensure all cells are linked."
    (is (every?
          #(not-any? empty? %)
          (backtracker/create
            (polar/init 10) [0 0] polar/neighbors polar/link)))))
