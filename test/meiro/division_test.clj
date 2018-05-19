(ns meiro.division-test
  (:require [clojure.test :refer [deftest testing is]]
            [meiro.core :as m]
            [meiro.division :as division]))


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
                    0 0 2 10 #'meiro.division/divide?)]
      (is (= 1
             (count (filter #(some #{:south} %) (get divided 0)))))
      (is (= 1
             (count (filter #(some #{:north} %) (get divided 1))))))))


(deftest divide-vertical-test
  (testing "Grid can be split vertically."
    (let [divided (#'meiro.division/divide-vertical
                    (#'meiro.division/link-all (m/init 10 2))
                    0 0 10 2 #'meiro.division/divide?)]
      (is (= 1
             (count (filter #(some #{:east} %)
                            (map (fn [row] (get row 0)) divided)))))
      (is (= 1
             (count (filter #(some #{:west} %)
                            (map (fn [row] (get row 1)) divided))))))))


(deftest division-fn-test
  (testing "Rate of 0% only returns true for height and width of 1."
    (let [div? (#'meiro.division/divide-fn 5 0.0)]
      (is (div? 1 8))
      (is (div? 8 1))
      (is (not (div? 2 2)))))
  (testing "Rate of 100% will create rooms below size threshold."
    (let [div? (#'meiro.division/divide-fn 4 1.0)]
      (is (div? 4 4))
      (is (not (div? 4 5)))
      (is (not (div? 5 4)))
      (is (not (div? 5 5))))))


(deftest create-test
  (testing "Ensure all cells are linked."
    (is (every? #(not-any? empty? %)
                (division/create (m/init 12 10)))))
   ; (testing "Even with rooms, all mazes should be perfect."
    ; (let [maze (create (m/init 25 25) 4 0.5)]
      ; (is (d/solution maze [0 0] [24 24]))
      ; (is (d/solution maze (m/random-pos maze) (m/random-pos maze)))))
   )
