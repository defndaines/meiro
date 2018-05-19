(ns meiro.hunt-and-kill-test
  (:require [clojure.test :refer [deftest testing is]]
            [meiro.core :as meiro]
            [meiro.hunt-and-kill :as hunt-and-kill]))

(deftest create-test
  (testing "Ensure all cells are linked."
    (is (every? #(not-any? empty? %)
                (hunt-and-kill/create (meiro/init 10 12))))))
