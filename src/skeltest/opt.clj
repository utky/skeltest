(ns skeltest.opt
  (require [clojure.tools.cli :as cli]))

(def cli-options
  [["-c" "--config CONFIG" "Path to configuration file"
    :id :config]
   ["-f" "--file FILE" "Path to input file"
    :id :input]
   ["-d" "--dir DIR" "Path to output"
    :id :output]])

(defn parse-cli-opts
  [args]
  (:options (cli/parse-opts args cli-options)))
