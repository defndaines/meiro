(ns meiro.ascii-test
	(:require [clojure.test :refer :all]
						[meiro.core :as m]
						[meiro.ascii :refer :all]))

(deftest cell-level-test
  (testing "Default rendering."
    (is (= '(\space \space \space \space)
           (#'meiro.ascii/cell-level [:east :south])))
    (is (= '(\space \space \space \|)
           (#'meiro.ascii/cell-level [:south]))))
  (testing "Pass value for inside."
    (is (= '(\space \1 \space \space)
           (#'meiro.ascii/cell-level [:east :south] " 1 ")))
    (is (= '(\space \2 \space \|)
           (#'meiro.ascii/cell-level [:south] " 2 ")))))

(deftest ascii-art
	(testing "Ensure rows and columns match."
		(is (= "+---+---+---+\n|   |   |   |\n+---+---+---+\n|   |   |   |\n+---+---+---+\n"
					 (render (m/init 2 3))))
		(is (= "+---+---+\n|   |   |\n+---+---+\n|   |   |\n+---+---+\n|   |   |\n+---+---+\n|   |   |\n+---+---+\n|   |   |\n+---+---+\n"
					 (render (m/init 5 2)))))
	(testing "Links are represented as gaps in the wall."
		(is (= "+---+---+\n|   |   |\n+---+---+\n|       |\n+---+---+\n"
					 (render (m/link (m/init 2 2) [1 1] [1 0]))))
		(is (= "+---+---+\n|   |   |\n+---+   +\n|   |   |\n+---+---+\n"
					 (render (m/link (m/init 2 2) [1 1] [0 1]))))))

(deftest include-distances
  (testing "When distances are provided."
    (let [maze [[[:east :south] [:west :east] [:west]]
                [[:north :south] [:east] [:west :south]]
                [[:north :east] [:west :east] [:north :west]]]
          distances [[0 1 2] [1 6 5] [2 3 4]]]
      (is (= "+---+---+---+\n| 0   1   2 |\n+   +---+---+\n| 1 | 6   5 |\n+   +---+   +\n| 2   3   4 |\n+---+---+---+\n"
             (render-distances maze distances))))))
