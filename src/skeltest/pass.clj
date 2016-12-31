(ns skeltest.pass
  (:require [clojure.string :as string]))

(defn empty-number?
  [n]
  (cond
    (number? n) false
    (string? n) (string/blank? n)
    :else (nil? n)))

(defn inherit-fn
  [entry]
  (let [field (first entry)
        frag (second entry)
        focus [:id field :number]]
    (if (empty-number? (:number frag))
      (fn [parent child]
        (update-in
          child
          focus 
          (fn [v] (get-in parent focus))))
      (fn [parent child] child))))

(defn inherit-fns
  [child]
    (let [id-record (:id child)]
      (map inherit-fn id-record)))

(defn inherit-id
  "Inherit parent id number fragments if it's absent on child record"
  [parent child]
  (reduce
    (fn [c f]
      (f parent c))
    child
    (inherit-fns child)))

(def initial-state
  {:results []
   :prev nil})

(defn- push-prev
  [state r]
  (let [prev (:prev state)]
    (if (nil? prev)
      (assoc state :prev r)
      (assoc state :prev (inherit-id prev r)))))

(defn- update-state
  [state r]
  (letfn [(conj-prev [state xs]
            (conj xs (:prev state)))]
    (-> state
        (push-prev r)
        (#(update % :results (partial conj-prev %))))))


