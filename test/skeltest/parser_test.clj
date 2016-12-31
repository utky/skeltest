(ns skeltest.parser-test
  (:require [clojure.test :refer :all]
            [skeltest.parser :refer :all]
            [clojure.java.io :as io]))
(def config
  {:sheet "testcases"
   :start-row 2
   :columns
     {:feature-id 0
      :feature 1
      :category-id 2
      :category 3
      :target-id 4
      :target 5
      :testcase-id 6
      :testcase 7
      :role 8
      :precondition 9
      :action 10
      :postcondition 11}})

(def test-data 
  "test-cases.xlsx")

(deftest parse-test
  (testing "parser can read data from spreadsheet"
    (with-open
      [i (io/input-stream test-data)]
      (let [parsed (parse config i)]
        (is
          (= 5
             (count parsed)))))))
