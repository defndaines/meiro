(defproject meiro "0.1.0-SNAPSHOT"
  :description "Working through Mazes for Programmers"
  :url "https://github.com/defndaines/meiro"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/data.generators "1.0.0"]]
  :profiles {:dev
             {:dependencies [[org.clojure/test.check "1.0.0"]]}})
