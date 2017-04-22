(ns meiro.core-test
	(:require [clojure.test :refer :all]
						[meiro.core :refer :all]
						[meiro.ascii :refer :all]))

(deftest ascii-art
	(testing "Ensure rows and columns match."
		(is (= "+---+---+---+\n|   |   |   |\n+---+---+---+\n|   |   |   |\n+---+---+---+\n"
					 (render (init-maze 2 3))))
		(is (= "+---+---+\n|   |   |\n+---+---+\n|   |   |\n+---+---+\n|   |   |\n+---+---+\n|   |   |\n+---+---+\n|   |   |\n+---+---+\n"
					 (render (init-maze 5 2)))))
	(testing "Links are represented as gaps in the wall."
		(is (= "+---+---+\n|   |   |\n+---+---+\n|       |\n+---+---+\n"
					 (render (link (init-maze 2 2) [1 1] [1 0]))))
		(is (= "+---+---+\n|   |   |\n+---+   +\n|   |   |\n+---+---+\n"
					 (render (link (init-maze 2 2) [1 1] [0 1]))))))
