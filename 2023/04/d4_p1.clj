(ns d4-p1
  (:require
   [clojure.string :as string]
   [clojure.set :as cset]))

(defn tap-> [x tag] (println {tag x}) x)
(defn tap->> [tag x] (println {tag x}) x)
(def tap tap->>)

(def lines (->> "input.txt"
             (slurp)
             (string/trim)
             (string/split-lines)))

(def tests
  (string/split-lines
"Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11"))

(defn exp
  [base power]
  (if (>= power 0)
    (int (.pow (BigInteger. base) power))
    0))

(defn parse-nums
  [nums-str]
  (->> (string/split nums-str #"\s+")
    (into #{})))

(defn line->card
  [card-line]
  (let [[_ winners scratches] (re-matches #"^Card\s+\d+: ([\d ]+)\s+\|\s+([\d ]+)$" card-line)
        winners               (parse-nums winners)
        scratches             (parse-nums scratches)
        correct               (cset/intersection winners scratches)
        value                 (->> (dec (count correct)) (exp "2"))]
    {:line      card-line
     :winners   winners
     :scratches scratches
     :correct   correct
     :value     value}))

(->> lines
  (reduce (fn [state card-line]
            (let [card (line->card card-line)]
              (-> state
                (update :cards conj card)
                (update :sum + (:value card))
                )))
    {:cards [] :sum 0})
  (tap :res)
  :sum
  )
