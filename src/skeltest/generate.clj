(ns skeltest.generate
  (:require [skeltest.syntax :as syntax])
  (:require [skeltest.pass :as pass])
  (:require [clojure.string :as string]))

(defprotocol Writable
  (write [this]))

(defn- get-
  [m k v]
  (let [r (get m k v)]
    (if (nil? r)
      v
      r)))

(defn- tab
  [coll]
  (map #(str "    " %) coll))

(defn- spl
  [text]
  (string/split-lines text))

(defn- lines
  [coll]
  (string/join "\n" coll))

(defn- words
  [coll]
  (string/join " " coll))

(defn- doc-indent
  [coll]
  (map #(str "...    " %) coll))


(defrecord Setting
  []
  Writable
  (write [this] this))

(defrecord Testcase
  [number tags desc subject given> when> then>]
  Writable
  (write [this]
    (lines
      [(:desc this)
       (lines
         (tab 
           (concat
             ["[Documentation]"]
             (doc-indent (spl (:desc this)))
             [(str "[Tags] " (words (:tags this)))]
             ["Given"]
             (tab (spl (get- this :given> "")))
             ["When"]
             (tab (spl (get- this :when> "")))
             ["Then"]
             (tab (spl (get- this :then> ""))))))])))

(defrecord Keyword
  []
  Writable
  (write [this] this))

(defrecord Robot
  [settings
   testcases
   keywords]
   Writable
  (write [this]
    (lines
      (concat
        ["*** Settings ***"]
        (:settings this)
        [""]
        ["*** Test cases ***"]
        (map write (:testcases this))
        [""]
        ["*** Keywords ***"]
        (map write (:keywords this))))))

(defrecord RobotFile
  [filename content])

(defn- scenario-step?
  "If testcase number is nil the case should be include scenario."
  [testcase]
  (nil? (get-in testcase [:id :testcase :number])))

(defn group-by-scenario
  "If testcase number is absent, it indicate the case should be merged into previous testcase as scenario step."
  [state testcase]
  (if (scenario-step? testcase)
    ;; merge current into previos list
    (let [prev (first state)
          tail (rest state)]
      (cond
        (seq? prev) (cons (cons (pass/inherit-id (first prev) testcase) prev) tail)
        :else        (cons (list (pass/inherit-id prev testcase) prev) tail)))
    (cons testcase state)))

(defn normalize
  "Convert string to decaptalized hyphen-delimited format
  \"Some String Content\" -> \"some-string-content\"
  "
  [text]
  (.toLowerCase (string/replace text #"\s+" "-")))

(defn tag-elements
  [testcase]
  (map (comp normalize :name second) (:id testcase)))

(defn make-case
  [testcase]
  (->Testcase
    (syntax/test-id-str testcase)
    (tag-elements testcase)
    (get-in testcase [:id :testcase :name])
    (:role testcase)
    (:pre testcase)
    (:action testcase)
    (:post testcase)))

(defn multi-cases
  [testcases]
  (->Robot
    []
    (map make-case testcases)
    []))

(defn single-case
  [testcase]
  (multi-cases [testcase]))

(defn- make-robot 
  [testcase]
  (if (or (seq? testcase) (vector? testcase))
    (multi-cases testcase)
    (single-case testcase)))

(defn- make-robot-file
  [robot]
  (->RobotFile
    (str (:number (first (:testcases robot))) ".robot")
    (write robot)))

(defn- reorder
  "reverse sequence 1-depth"
  [data]
  (reverse (map #(if (or (seq? %) (vector? %)) (reverse %) %) data)))

(defn buf
  [data]
  (doall (map println data))
  data)

(defn generate
  [data]
  (->> data
       (reduce group-by-scenario [])
       (reorder)
       (map (comp make-robot-file make-robot))))
