(ns meiro.eller-test
  (:require [clojure.test :refer [deftest testing is]]
            [meiro.graph :as graph]
            [meiro.eller :refer :all]))


(deftest for-forests-test
  (testing "Get the forests for a given row."
    (is (= #{{:width 3 :height 1 :nodes #{[0 0]} :edges []}
             {:width 3 :height 1 :nodes #{[1 0]} :edges []}
             {:width 3 :height 1 :nodes #{[2 0]} :edges []}}
           (for-forests #{} 0 3 1)))
    (is (= #{{:width 3 :height 3 :nodes #{[0 2]} :edges []}
             {:width 3 :height 3 :nodes #{[1 2]} :edges []}
             {:width 3 :height 3 :nodes #{[2 2]} :edges []}}
           (for-forests #{} 2 3 3)))
    (is (= #{{:width 3 :height 1 :nodes #{[0 0]} :edges []}
             {:width 3 :height 1 :nodes #{[1 0]} :edges []}
             {:width 3 :height 1 :nodes #{[2 0]} :edges []}}
           (for-forests
             #{{:width 3 :height 1 :nodes #{[1 0]} :edges []}}
             0 3 1)))
    (is (= #{{:width 3 :height 2 :nodes #{[0 1]}
              :edges []}
             {:width 3 :height 2 :nodes #{[1 1] [0 0] [1 0]}
              :edges [[[0 0] [1 0]] [[1 0] [1 1]]]}
             {:width 3 :height 2 :nodes #{[2 1] [2 0]}
              :edges [[[2 0] [2 1]]]}}
           (for-forests
             #{{:width 3 :height 2 :nodes #{[1 1] [0 0] [1 0]}
                :edges [[[0 0] [1 0]] [[1 0] [1 1]]]}
               {:width 3 :height 2 :nodes #{[2 1] [2 0]}
                :edges [[[2 0] [2 1]]]}}
             1 3 2)))))


(deftest merge-all-test
  (testing "Merge multiple forests into a single forest."
    (is (= {:width 3 :height 1 :nodes #{[0 0] [1 0] [2 0]}
            :edges [[[0 0] [1 0]] [[1 0] [2 0]]]}
           (merge-all
             #{{:width 3 :height 1 :nodes #{[0 0]} :edges []}
               {:width 3 :height 1 :nodes #{[1 0]} :edges []}
               {:width 3 :height 1 :nodes #{[2 0]} :edges []}})))
    (let [forest (merge-all
                   #{{:width 3 :height 2 :nodes #{[0 1]}
                      :edges []}
                     {:width 3 :height 2 :nodes #{[1 1] [0 0] [1 0]}
                      :edges [[[0 0] [1 0]] [[1 0] [1 1]]]}
                     {:width 3 :height 2 :nodes #{[2 1] [2 0]}
                      :edges [[[2 0] [2 1]]]}})]
      (is (= 3 (:width forest)))
      (is (= 2 (:height forest)))
      (is (filter #(= % [[0 0] [1 0]]) (:edges forest)))
      (is (filter #(= % [[1 0] [1 1]]) (:edges forest)))
      (is (filter #(= % [[2 0] [2 1]]) (:edges forest)))
      (is (= (dec (* (:width forest) (:height forest)))
             (count (:edges forest))))
      (is (= (set (for [x (range (:width forest))
                        y (range (:height forest))]
                    [x y]))
             (:nodes forest))))))


(deftest horizontal-link-test
  (testing "Link cells in a row."
    (is (= #{{:width 3 :height 1
              :nodes #{[0 0] [1 0] [2 0]}
              :edges [[[0 0] [1 0]] [[1 0] [2 0]]]}}
           (#'meiro.eller/link-horizontal
             (for-forests #{} 0 3 1)
             0 1.0))))
  (testing "No links in a row."
    (let [forests (for-forests #{} 0 3 1)]
      (is (= forests
             (#'meiro.eller/link-horizontal forests 0 0.0))))))


(deftest vertical-link-test
  (testing "Link cells vertically."
    (is (= #{{:width 2 :height 2 :nodes #{[0 0] [0 1]} :edges [[[0 0] [0 1]]]}
             {:width 2 :height 2 :nodes #{[1 0] [1 1]} :edges [[[1 0] [1 1]]]}}
           (#'meiro.eller/link-vertical
             (for-forests #{} 0 2 2)
             0 1.0))))
  (testing "Only one cell per corridor links south."
    (is (= 2
           (-> (for-forests #{} 0 2 2)
             (#'meiro.eller/link-horizontal 0 1.0)
             (#'meiro.eller/link-vertical 0 1.0)
             first
             :edges
             count)))))


(deftest create-test
  (let [width 2
        height 5
        forest (create width height)]
    (testing "Creating a maze using Eller's Algorithm."
      (is (= (* width height)
             (count (:nodes forest))))
      (is (= (dec (* width height))
             (count (:edges forest)))))
    (testing "Ensure all cells are linked."
      (is (every?
            #(not-any? empty? %)
            (graph/forest-to-maze forest))))))
