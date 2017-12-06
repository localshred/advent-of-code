'{:dependencies [[org.clojure/clojure "1.8.0"]]}

(ns advent.2017.02.part1
  (:require [clojure.string :as string]))

(defn tap [x] (do (println x) x))

(defn split
  [delimiter value]
  (string/split value delimiter))

(defn split-line
  [line]
  (->> line
       (split #"\s+")
       (map #(Integer/parseInt (str %)))))

(def input (->> "./2017/02/input.txt"
               (slurp)
               (string/trim)
               (split #"\n")
               (map split-line)))

(defn compute-line-difference
  [numbers]
    (-
     (apply max numbers)
     (apply min numbers)))

(def result (->> input
                 (map compute-line-difference)
                 (tap)
                 (reduce + 0)))

(println result)
