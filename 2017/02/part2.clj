'{:dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/math.combinatorics "0.1.4"]]}

(ns advent.2017.02.part2
  (:require [clojure.math.combinatorics :as combo]
            [clojure.string :as string]))

(defn tap [x] (do (println x) x))

(defn split
  [delimiter value]
  (string/split value delimiter))

(defn combination-pairs
  [numbers]
  (combo/combinations numbers 2))

(defn split-line
  [line]
  (->> line
       (split #"\s+")
       (map #(Integer/parseInt (str %)))
       (combination-pairs)))

(def input (->> "./2017/02/input.txt"
               (slurp)
               (string/trim)
               (split #"\n")
               (map split-line)))

(defn whole-quotient
  [pair]
  (let [a (apply / pair)
        b (apply / (reverse pair))]
    (cond
      (integer? a) a
      (integer? b) b
      :else nil)))

(def result (->> input
                 (map (partial some whole-quotient))
                 (tap)
                 (reduce + 0)))

(println result)
