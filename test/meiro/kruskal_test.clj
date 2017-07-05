(ns meiro.kruskal-test
  (:require [clojure.test :refer :all]
            [meiro.kruskal :refer :all]
            [meiro.graph :as graph]))


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
          all (graph/all-edges 5 5)
          without (#'meiro.kruskal/rm-weave-edges all weave-edges)]
      (is (every?
            (fn [e] (not-any? #{e} without))
            weave-edges)))))


(deftest create-test
  (testing "Creating a maze using Kruskal's Algorithm."
    (is (= (dec (* 8 12))
           (count (:edges (create 8 12))))))
  (testing "Ensure all cells are linked."
    (is (every?
          #(not-any? empty? %)
          (graph/forest-to-maze (create 10 12))))))


(deftest weave-test
  (let [forests (graph/init-forests 3 3)]
    (testing "Can add weaves to forests for a Kruskal's maze."
      (is (= [[[0 1] [2 1]]]
             (-> forests
                 (weave [1 1] :horizontal)
                 (graph/find-forest [0 1])
                 :edges)))
      (is (= [[[1 0] [1 1]] [[1 1] [1 2]]]
             (-> forests
                 (weave [1 1] :horizontal)
                 (graph/find-forest [1 1])
                 :edges)))
      (is (= [[[0 1] [1 1]] [[1 1] [2 1]]]
             (-> forests
                 (weave [1 1] :vertical)
                 (graph/find-forest [1 1])
                 :edges)))
      (is (= [[[1 0] [1 2]]]
             (-> forests
                 (weave [1 1] :vertical)
                 (graph/find-forest [1 0])
                 :edges))))
    (testing "Invalid requests return original forests."
      (is (= forests
             (weave forests [0 0])))
      (is (= forests
             (weave forests [0 1]))))))
