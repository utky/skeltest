(ns skeltest.parser
  (:require [dk.ative.docjure.spreadsheet :as docj]
            [skeltest.syntax :as syntax]))

(defn- cell-value
  "Return string value in cell"
  [row index]
  (docj/read-cell
    (.getCell row index)))

(defn- col-value
  "Return string value in cell in specified row.
   row: Row
   col-name: Keyword
  "
  [columns row col-name]
  (let [index (col-name columns)]
    (cell-value row index)))

(def col-names
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



(defn- column-parser
  [config]
  (let [columns (:columns config)]
    (fn [row]
      (->> col-names
        (map (partial col-value columns row))
        (apply syntax/test-case)))))

(defn parse
  "parser from filename or input stream."
  [config input]
  (let [sub-parser (column-parser config)]
    (->> (docj/load-workbook input)
         (docj/select-sheet (:sheet config))
         (docj/row-seq)
         (drop (dec (:start-row config)))
         (map sub-parser))))
