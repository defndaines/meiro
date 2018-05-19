(ns meiro.aldous-broder-test
  (:require [clojure.test :refer [deftest testing is]]
            [meiro.core :as meiro]
            [meiro.aldous-broder :as ab]))

(deftest create-test
  (testing "Ensure all cells are linked."
    (is (every? #(not-any? empty? %)
                (ab/create (meiro/init 10 12))))))
