(ns meiro.core-test
  (:require [clojure.test :refer :all]
            [meiro.core :refer :all]))

(deftest init-test
  (testing "First level is row, each row contains columns."
    (is (= 5 (count (init-maze 5 3))))
    (is (every? #(= 4 (count %)) (init-maze 5 4)))))

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
    (is (adjacent? (init-maze 5 5) [0 0] [0 1]))
    (is (adjacent? (init-maze 5 5) [0 0] [1 0]))
    (is (not (adjacent? (init-maze 5 5) [0 0] [0 2])))
    (is (not (adjacent? (init-maze 5 5) [0 0] [2 0]))))
  (testing "false if cell outside maze."
    (is (not (adjacent? (init-maze 5 5) [0 0] [-1 0])))
    (is (not (adjacent? (init-maze 5 5) [0 0] [0 -1])))
    (is (not (adjacent? (init-maze 5 5) [4 0] [5 0])))
    (is (not (adjacent? (init-maze 5 5) [0 4] [0 5])))))

(deftest link-test
  (testing "Adjacent cells linked by opposite directions."
    (let [above [2 2]
          below [3 2]
          m (link (init-maze 6 4) below above)]
      (is (some (comp = :north) (get-in m below)))
      (is (some (comp = :south) (get-in m above))))
    (let [left [1 2]
          right [1 3]
          m (link (init-maze 6 4) left right)]
      (is (some (comp = :east) (get-in m left)))
      (is (some (comp = :west) (get-in m right)))))
  (testing "Non-adjacent cells do not link."
    (let [m (init-maze 5 5)]
      (is (= m (link m [0 0] [1 1]))))))
