(ns meiro.backtracker-test
  (:require [clojure.test :refer :all]
            [meiro.core :refer :all]
            [meiro.backtracker :refer :all]))

(deftest create-test
  (testing "Ensure all cells are linked."
    (is (every? #(not-any? empty? %) (create (init 10 12))))))
