(ns skeltest.syntax)

(defrecord Testcase
  [ id desc role pre action post ])

(defrecord TestId
  [ feature category target testcase ])

(defrecord IdFragment
  [ number name ])

(defn test-id
  [ feature-id feature
    category-id category
    target-id  target
    testcase-id testcase ]
  (->TestId
    (->IdFragment feature-id feature)
    (->IdFragment category-id category)
    (->IdFragment target-id target)
    (->IdFragment testcase-id testcase)))

(defn test-case
  [ feature-id feature
    category-id category
    target-id  target
    testcase-id testcase
    role
    precondition
    action
    postcondition]
  (->Testcase
    (test-id 
      feature-id feature
      category-id category
      target-id  target
      testcase-id testcase)
    testcase
    role
    precondition
    action
    postcondition))

(defn join-id
  [test-id]
  (let
    [ fields [ :feature :category :target :testcase ]
      numbers (map (comp int :number second)
                   (select-keys test-id fields)) ]
    (reduce
      (fn [x y]
        (str x "-" y))
      numbers)))

(defn test-id-str
  [testcase]
    (join-id (:id testcase)))

