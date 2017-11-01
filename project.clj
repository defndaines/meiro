(defproject meiro "0.1.0-SNAPSHOT"
  :description "Working through Mazes for Programmers"
  :url "https://github.com/defndaines/meiro"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-beta4"]
                 [org.clojure/data.generators "0.1.2"]]
  :profiles {:dev
             {:dependencies [[org.clojure/test.check "0.9.0"]]}})
