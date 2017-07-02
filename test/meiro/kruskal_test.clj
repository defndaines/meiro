(ns meiro.kruskal-test
  (:require [clojure.test :refer :all]
            [meiro.kruskal :refer :all]))


(deftest edges-test
  (testing "Initialize all the edges in a grid."
    (is (= (+ (* 4 (dec 5)) (* (dec 4) 5))
           (count (#'meiro.kruskal/all-edges 4 5))))
    (is (= (- (* 2 15 6) 15 6)
           (count (#'meiro.kruskal/all-edges 15 6))))))


(deftest init-forests-test
  (testing "Forest initialization for a grid."
    (is (= (* 5 4)
           (count (init-forests 5 4))))
    (is (= (* 6 15)
           (count (init-forests 6 15))))))


(deftest find-forest-test
  (testing "Able to find forests by position."
    (let [forests (#'meiro.kruskal/init-forests 6 15)]
      (is (= {:nodes #{[4 13]} :edges []}
             (#'meiro.kruskal/find-forest forests [4 13])))
      (is (nil?
            (#'meiro.kruskal/find-forest forests [4 15]))))))


(deftest merge-forests-test
  (testing "Merge two forests with a shared edge."
    (is (= {:nodes #{[2 4] [2 5]} :edges [[[2 4] [2 5]]]}
           (#'meiro.kruskal/merge-forests
             {:nodes #{[2 4]} :edges []}
             {:nodes #{[2 5]} :edges []}
             [[2 4] [2 5]])))))


(deftest weave-edges-test
  (testing "Able to identify non-adjacent edges in forests."
    (let [forests #{{:nodes #{[4 3]} :edges []} {:nodes #{[4 4]} :edges []}
                    {:nodes #{[2 3]}:edges []} {:nodes #{[2 4]} :edges []}
                    {:nodes #{[2 0]} :edges []} {:nodes #{[0 0]} :edges []}
                    {:nodes #{[1 0] [1 2]} :edges [[[1 0] [1 2]]]}
                    {:nodes #{[1 4]} :edges []} {:nodes #{[4 2]} :edges []}
                    {:nodes #{[0 2]} :edges []}
                    {:nodes #{[1 1] [4 1] [3 1] [2 1] [0 1]}
                     :edges [[[0 1] [1 1]] [[1 1] [2 1]]
                             [[2 1] [3 1]] [[3 1] [4 1]]]}
                    {:nodes #{[3 0] [3 2]} :edges [[[3 0] [3 2]]]}
                    {:nodes #{[0 3]} :edges []} {:nodes #{[2 2]} :edges []}
                    {:nodes #{[1 3]} :edges []} {:nodes #{[3 3]} :edges []}
                    {:nodes #{[3 4]} :edges []} {:nodes #{[4 0]} :edges []}
                    {:nodes #{[0 4]} :edges []}}]
      (is (= [[[1 0] [1 1]] [[1 1] [1 2]] [[3 0] [3 1]] [[3 1] [3 2]]]
             (#'meiro.kruskal/weave-edges forests))))))


(deftest rm-weave-edges-test
  (testing "Remove weave edges for collection of edges."
    (let [weave-edges [[[1 0] [1 1]] [[1 1] [1 2]] [[3 0] [3 1]] [[3 1] [3 2]]]
          all (#'meiro.kruskal/all-edges 5 5)
          without (#'meiro.kruskal/rm-weave-edges all weave-edges)]
      (is (every?
            (fn [e] (not-any? #{e} without))
            weave-edges)))))


(deftest create-test
  (testing "Creating a maze using Kruskal's Algorithm."
    (is (= (dec (* 8 12))
           (count (create 8 12)))))
  (testing "Ensure all cells are linked."
    (is (every?
          #(not-any? empty? %)
          (edges-to-grid (create 10 12) 10 12)))))


(deftest weave-test
  (let [forests (init-forests 3 3)]
    (testing "Can add weaves to forests for a Kruskal's maze."
      (is (= [[[0 1] [2 1]]]
             (-> forests
                 (weave [1 1] :horizontal)
                 (#'meiro.kruskal/find-forest [0 1])
                 :edges)))
      (is (= [[[1 0] [1 1]] [[1 1] [1 2]]]
             (-> forests
                 (weave [1 1] :horizontal)
                 (#'meiro.kruskal/find-forest [1 1])
                 :edges)))
      (is (= [[[0 1] [1 1]] [[1 1] [2 1]]]
             (-> forests
                 (weave [1 1] :vertical)
                 (#'meiro.kruskal/find-forest [1 1])
                 :edges)))
      (is (= [[[1 0] [1 2]]]
             (-> forests
                 (weave [1 1] :vertical)
                 (#'meiro.kruskal/find-forest [1 0])
                 :edges))))
    (testing "Invalid requests return original forests."
      (is (= forests
             (weave forests [0 0])))
      (is (= forests
             (weave forests [0 1]))))))
