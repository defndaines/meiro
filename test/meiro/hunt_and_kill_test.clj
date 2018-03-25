(ns meiro.hunt-and-kill-test
  (:require [clojure.test :refer [deftest testing is]]
            [meiro.core :refer :all]
            [meiro.hunt-and-kill :refer :all]))

(deftest create-test
  (testing "Ensure all cells are linked."
    (is (every? #(not-any? empty? %) (create (init 10 12))))))
