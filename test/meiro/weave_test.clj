(ns meiro.weave-test
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer :all]
            [meiro.core :as m]
            [meiro.weave :refer :all]))


(deftest east-west-test
  (testing "Check for east-west corridors."
    (is (#'meiro.weave/east-west? [:east :west]))
    (is (#'meiro.weave/east-west? [:west :east]))
    (is (not (#'meiro.weave/east-west? [:east :west :south])))
    (is (not (#'meiro.weave/east-west? [:east :west :north])))
    (is (not (#'meiro.weave/east-west? [:south :north])))
    (is (not (#'meiro.weave/east-west? [:east])))
    (is (not (#'meiro.weave/east-west? [:west])))))


(deftest north-south-test
  (testing "Check for north-south corridors."
    (is (#'meiro.weave/north-south? [:north :south]))
    (is (#'meiro.weave/north-south? [:south :north]))
    (is (not (#'meiro.weave/north-south? [:north :south :east])))
    (is (not (#'meiro.weave/north-south? [:north :south :west])))
    (is (not (#'meiro.weave/north-south? [:east :west])))
    (is (not (#'meiro.weave/north-south? [:north])))
    (is (not (#'meiro.weave/north-south? [:south])))))


(deftest cells-to-test
  (testing "Gets a sequence of valid positions from a starting cell."
    (is (= [[0 3] [0 2] [0 1] [0 0]]
           (#'meiro.weave/cells-to (m/init 1 5) m/west [0 4])))
    (is (= [[0 1] [0 2] [0 3] [0 4]]
           (#'meiro.weave/cells-to (m/init 1 5) m/east [0 0])))
    (is (= [[0 3] [0 4]]
           (#'meiro.weave/cells-to (m/init 1 5) m/east [0 2])))
    (is (= [[4 1] [3 1] [2 1] [1 1] [0 1]]
           (#'meiro.weave/cells-to (m/init 6 2) m/north [5 1])))
    (is (= [[2 1] [3 1] [4 1]]
           (#'meiro.weave/cells-to (m/init 5 2) m/south [1 1])))))


(deftest cell-west-test
  (testing "Pick up a cell to the west if available."
    (is (nil?
          (cell-west
            [[[:north :south] [:north :south] [:start]]]
            [0 2])))
    (is (= [0 0]
           (cell-west
             [[[] [:north :south] [:start]]]
             [0 2])))
    (is (= [0 0]
           (cell-west
             [[[] [:north :south] [:north :south] [:north :south] [:start]]]
             [0 4])))
    (is (nil?
          (cell-west
            [[[] [:north] [:north :south] [:north :south] [:start]]]
            [0 4])))
    (is (= [0 1]
           (cell-west
             [[[] [] [:north :south] [:north :south] [:start]]]
             [0 4])))))


(deftest cell-east-test
  (testing "Pick up a cell to the east if available."
    (is (nil?
          (cell-east
            [[[:start] [:north :south] [:north :south]]]
            [0 0])))
    (is (= [0 3]
           (cell-east
             [[[:north :south] [:start] [:north :south] []]]
             [0 1])))))


(deftest cell-north-test
  (testing "Pick up a cell to the north if available."
    (is (nil?
          (cell-north
            [[[:east :west]] [[:east :west]] [[:start]]]
            [2 0])))
    (is (= [0 0]
           (cell-north
             [[[]] [[:east :west]] [[:start]]]
             [2 0])))))


(deftest cell-south-test
  (testing "Pick up a cell to the south if available."
    (is (nil?
          (cell-south
            [[[:start]] [[:east :west]] [[:east :west]]]
            [0 0])))
    (is (= [2 0]
           (cell-south
             [[[:start]] [[:east :west]] [[]]]
             [0 0])))))


(deftest neighbors-test
  (testing "Weave cells are available as neighbors when appropriate."
    (let [maze [[[] [] [] [] []]
                [[] [] [:east :west] [] []]
                [[] [:north :south] [] [:north :south] []]
                [[] [] [:east :west] [] []]
                [[] [] [] [] []]]]
      (is (= [[0 2] [1 2] [3 2] [4 2] [2 3] [2 4] [2 0] [2 1]]
             (neighbors maze [2 2]))))
    (let [maze [[[] [] [:north] [] []]
                [[] [] [:east :west] [] []]
                [[:west] [:north :south] [] [:north :south] [:east]]
                [[] [] [:east :west] [] []]
                [[] [] [:south] [] []]]]
      (is (= [[1 2] [3 2] [2 3] [2 1]]
             (neighbors maze [2 2])))) ))
