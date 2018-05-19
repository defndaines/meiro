(ns meiro.backtracker-test
  (:require [clojure.test :refer [deftest testing is]]
            [meiro.core :as meiro]
            [meiro.backtracker :as bt]))

(deftest create-test
  (testing "Ensure all cells are linked."
    (is (every? #(not-any? empty? %) (bt/create (meiro/init 10 12))))))

(deftest create-with-mask-test
  (testing "Some cells can be masked and a valid maze generates."
    (let [grid (-> (meiro/init 10 10)
                   (update-in [0 4] conj :mask)
                   (update-in [0 6] conj :mask))
          maze (bt/create grid)]
      (is (= [:mask] (get-in maze [0 4])))
      (is (= [:south] (get-in maze [0 5]))) ; Cell not orphaned
      (is (= [:mask] (get-in maze [0 6])))
      (is (every? #(not-any? empty? %) maze)))))
