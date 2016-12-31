(ns skeltest.config
  (require [clojure.data.json :as json]))

(def columns
  [:feature-id
   :feature
   :category-id
   :category
   :target-id
   :target
   :testcase-id
   :testcase
   :role
   :precondition
   :action
   :postcondition])

(defrecord Config
  [sheet
   start-row
   columns])

(defn parse-config
  [path]
  (-> (slurp path)
      (json/read-str :key-fn keyword)))
