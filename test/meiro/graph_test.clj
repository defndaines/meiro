(ns meiro.graph-test
  (:require [clojure.test :refer [deftest testing is]]
            [meiro.graph :refer :all]))


(deftest edges-test
  (testing "Initialize all the edges in a grid."
    (is (= (+ (* 4 (dec 5)) (* (dec 4) 5))
           (count (all-edges 4 5))))
    (is (= (- (* 2 15 6) 15 6)
           (count (all-edges 15 6))))))


(deftest init-forests-test
  (testing "Forest initialization for a grid."
    (is (= (* 5 4)
           (count (init-forests 5 4))))
    (is (= (* 6 15)
           (count (init-forests 6 15))))))


(deftest find-forest-test
  (testing "Able to find forests by position."
    (let [forests (init-forests 6 15)]
      (is (= {:width 6 :height 15 :nodes #{[4 13]} :edges []}
             (find-forest forests [4 13])))
      (is (nil?
            (find-forest forests [4 15]))))))


(deftest merge-forests-test
  (testing "Merge two forests with a shared edge."
    (is (= {:width 3 :height 6 :nodes #{[2 4] [2 5]} :edges [[[2 4] [2 5]]]}
           (merge-forests
             {:width 3 :height 6 :nodes #{[2 4]} :edges []}
             {:width 3 :height 6 :nodes #{[2 5]} :edges []}
             [[2 4] [2 5]]))))
  (testing "Set width and height if missing from one forest."
    (is (= 4
           (:width (merge-forests
                     {:nodes #{[1 0]} :edges []}
                     {:width 4 :height 5 :nodes #{[0 0]} :edges []}
                     [[0 0] [1 0]]))))
    (is (= 4
           (:width (merge-forests
                     {:width 4 :height 5 :nodes #{[0 0]} :edges []}
                     {:nodes #{[1 0]} :edges []}
                     [[0 0] [1 0]]))))
    (is (= 5
           (:height (merge-forests
                      {:nodes #{[1 0]} :edges []}
                      {:width 4 :height 5 :nodes #{[0 0]} :edges []}
                      [[0 0] [1 0]]))))
    (is (= 5
           (:height (merge-forests
                      {:width 4 :height 5 :nodes #{[0 0]} :edges []}
                      {:nodes #{[1 0]} :edges []}
                      [[0 0] [1 0]]))))))
