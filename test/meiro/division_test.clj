(ns meiro.division-test
  (:require [clojure.test :refer :all]
            [meiro.core :as m]
            [meiro.division :refer :all]))


(deftest link-all-test
  (testing "All cells linked."
    (is (= [[[:south :east] [:south :east :west] [:south :west]]
            [[:north :south :east] [:north :south :east :west]
             [:north :south :west]]
            [[:north :east] [:north :east :west] [:north :west]]]
           (#'meiro.division/link-all (m/init 3 3))))))


(deftest divide-horizontal-test
  (testing "Grid can be split horizontally."
    (let [divided (#'meiro.division/divide-horizontal
                    (#'meiro.division/link-all (m/init 2 10))
                    0 0 2 10)]
      (is (= 1
             (count (filter #(some #{:south} %) (get divided 0)))))
      (is (= 1
             (count (filter #(some #{:north} %) (get divided 1))))))))


(deftest divide-vertical-test
  (testing "Grid can be split vertically."
    (let [divided (#'meiro.division/divide-vertical
                    (#'meiro.division/link-all (m/init 10 2))
                    0 0 10 2)]
      (is (= 1
             (count (filter #(some #{:east} %)
                            (map (fn [row] (get row 0)) divided)))))
      (is (= 1
             (count (filter #(some #{:west} %)
                            (map (fn [row] (get row 1)) divided))))))))


(deftest create-test
  (testing "Ensure all cells are linked."
    (is (every? #(not-any? empty? %)
                (create (m/init 12 10))))))
