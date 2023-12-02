(ns d1-p2
  (:require
   [clojure.string :as string]))

(defn split
  [delimiter value]
  (string/split value delimiter))

(def lines (->> "input.txt"
             (slurp)
             (string/trim)
             (split #"\n")))

(def nums
  {"one"   "1"
   "two"   "2"
   "three" "3"
   "four"  "4"
   "five"  "5"
   "six"   "6"
   "seven" "7"
   "eight" "8"
   "nine"  "9"})

(def first-pattern
  #"^.*?(1|2|3|4|5|6|7|8|9|one|two|three|four|five|six|seven|eight|nine)")

(def last-pattern
  #"^.*(1|2|3|4|5|6|7|8|9|one|two|three|four|five|six|seven|eight|nine).*?$")

(defn get-digit
  [pattern line]
  (as-> line $
    (re-find pattern $)
    (last $)
    (get nums $ $)))

(defn line->int
  [line]
  (let [a    (get-digit first-pattern line)
        b    (get-digit last-pattern line)
        pair (str a b)
        res  (Integer/parseInt pair)]
    (println (format "%d %s" res line))
    res))

;; Results
(->> lines
  (map line->int)
  (reduce + 0))

;; Some of the trickier lines to test
(for [[expected line]
      [[68 "six6fourjghzroneightf"]
       [13 "jrgoneightnddmmchbmmklk847three"]
       [88 "pf8oneoneightjgl"]
       [48 "hzbfour63nttfktqjzjhponeightcz"]
       ]]
  (let [c (line->int line)]
    (when-not (= expected c)
      (println (format "FAIL! Expected %d, got %d (%s)" expected c line)))))
