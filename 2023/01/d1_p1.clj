(ns d1-p1
  (:require
   [clojure.string :as string]))

(defn tap [x] (println x) x)

(defn split
  [delimiter value]
  (string/split value delimiter))

(def lines (->> "input.txt"
             (slurp)
             (string/trim)
             (split #"\n")))

(defn line-to-int
  [line]
  (let [digits      (string/replace line #"[^\d]" "")
        first-digit (first digits)
        last-digit  (last digits)]
    (Integer/parseInt (str first-digit last-digit))))

(->> lines
  (map line-to-int)
  (reduce + 0))
