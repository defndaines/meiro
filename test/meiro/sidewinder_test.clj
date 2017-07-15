(ns meiro.sidewinder-test
  (:require [clojure.test :refer :all]
            [meiro.core :refer :all]
            [meiro.sidewinder :refer :all]))


(deftest create-test
  (testing "Ensure all cells are linked."
    (is (every? #(not-any? empty? %) (create (init 10 12))))))


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
           (last-row 4)))))


(deftest create-lazy-test
  (testing "Build a maze using infinite approach."
    (let [maze (create-lazy 8)]
      (is (= 8
             (count (first (drop 25 maze))))))))
