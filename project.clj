(defproject skeltest "0.1.0-SNAPSHOT"
  :description "Read test cases from Spread sheet and generate skeltons."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [dk.ative/docjure "1.11.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/tools.cli "0.3.5"]]
  :main ^:skip-aot skeltest.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
