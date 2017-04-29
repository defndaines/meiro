(ns meiro.ascii-test
	(:require [clojure.test :refer :all]
						[meiro.core :as m]
						[meiro.ascii :refer :all]))

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
