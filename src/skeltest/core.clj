(ns skeltest.core
  (:require [skeltest.opt :as opt]
            [skeltest.config :as config]
            [skeltest.parser :as parser]
            [skeltest.pass :as pass]
            [skeltest.generate :as gen]
            [clojure.string :as string])
  (:gen-class))

(defrecord App
  [options config data])

(defn- init-app
  "Initialize app state"
  [options]
  (->App options nil nil))

(defn- read-config
  [app]
  (assoc app
         :config
         (config/parse-config
           (get-in app [:options :config]))))

(defn- parse-data
  [app]
  (assoc app
          :data
          (parser/parse
            (:config app)
            (get-in app [:options :input]))))

(defn- process-data
  [app]
  (update app
          :data
          ;;(pass/inherit-id)))
          identity))

(defn- translate
  [app]
  (update app
          :data
          gen/generate))

(def sep
  java.io.File/separator)

(defn dirname
  [path]
  (if (string/ends-with? path sep)
    path
    (str path sep)))

(defn- write-file
  [app]
  (letfn [(write-down
            [skel]
            (let [dir (dirname (get-in app [:options :output]))
                  outfile (str dir (:filename skel))]
              (spit outfile (:content skel))))]
    (doall (map write-down (:data app)))))

(defn -main
  "Read test cases from file and generate test skelton"
  [& args]
  (->> args
       (opt/parse-cli-opts)
       (init-app)
       (read-config)
       (parse-data)
       (process-data)
       (translate)
       (write-file)))
