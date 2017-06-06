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
