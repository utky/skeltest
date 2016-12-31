(ns skeltest.syntax-test
  (:require [clojure.test :refer :all]
            [skeltest.syntax :refer :all]))

(def test-data
  [ "1" "Feature"
    "2" "Category"
    "3" "Target"
    "4" "Test case"
    "role"
    "precondition"
    "action"
    "postcondition"])

(deftest deserialize-test
  (testing "Deserialize tuple to testcase"
    (is
      (= "1-2-3-4"
         (test-id-str (apply test-case test-data))))))
