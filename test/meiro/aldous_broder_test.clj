(ns meiro.aldous-broder-test
  (:require [clojure.test :refer [deftest testing is]]
            [meiro.core :refer :all]
            [meiro.aldous-broder :refer :all]))

(deftest create-test
  (testing "Ensure all cells are linked."
    (is (every? #(not-any? empty? %) (create (init 10 12))))))
