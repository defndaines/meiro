(ns meiro.wilson-test
  (:require [clojure.test :refer [deftest testing is]]
            [meiro.core :as meiro]
            [meiro.wilson :as wilson]))


(deftest walk-test
  (testing "Path includes one visited cell."
    (let [maze (meiro/init 8 8)]
      (is (= [0 0]
             (last (#'meiro.wilson/walk
                     maze
                     (remove #{[0 0]} (meiro/all-positions maze))))))
      (is (= [3 4]
             (last (#'meiro.wilson/walk
                     maze
                     (remove #{[3 4]} (meiro/all-positions maze)))))))))

(deftest create-test
  (testing "Ensure all cells are linked."
    (is (every? #(not-any? empty? %) (wilson/create (meiro/init 10 12))))))
