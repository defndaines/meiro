(ns meiro.backtracker-test
  (:require [clojure.test :refer :all]
            [meiro.core :refer :all]
            [meiro.backtracker :refer :all]))

(deftest create-test
  (testing "Ensure all cells are linked."
    (is (every? #(not-any? empty? %) (create (init 10 12))))))

(deftest create-with-mask-test
  (testing "Some cells can be masked and a valid maze generates."
    (let [grid (-> (init 10 10)
                   (update-in [0 4] conj :mask)
                   (update-in [0 6] conj :mask))
          maze (create grid)]
      (is (= [:mask] (get-in maze [0 4])))
      ;; TODO non-deterministic ... which means there's a bug.
      (is (= [:south] (get-in maze [0 5]))) ; Cell not orphaned
      (is (= [:mask] (get-in maze [0 6])))
      ;; TODO non-deterministic ...
      (is (every? #(not-any? empty? %) maze)))))
