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
    (is (nil? (#'meiro.weave/cell-west
                [[[:north :south] [:north :south] [:start]]]
                [0 2])))
    (is (= [0 0]
           (#'meiro.weave/cell-west
             [[[] [:north :south] [:start]]]
             [0 2])))
    (is (= [0 0]
           (#'meiro.weave/cell-west
             [[[] [:north :south] [:north :south] [:north :south] [:start]]]
             [0 4])))
    (is (nil? (#'meiro.weave/cell-west
                [[[] [:north] [:north :south] [:north :south] [:start]]]
                [0 4])))
    (is (= [0 1]
           (#'meiro.weave/cell-west
             [[[] [] [:north :south] [:north :south] [:start]]]
             [0 4])))))
