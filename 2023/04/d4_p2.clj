(ns d4-p2
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

(defn parse-nums
  [nums-str]
  (->> (string/split nums-str #"\s+")
    (into #{})))

(defn line->card
  [card-line]
  (let [[_ n left right] (re-matches #"^Card\s+(\d+): ([\d ]+)\s+\|\s+([\d ]+)$" card-line)
        left             (parse-nums left)
        right            (parse-nums right)
        correct          (cset/intersection left right)
        amt              (count correct)]
    {:n       (Integer/parseInt n)
     :amt     amt
     :left    left
     :right   right
     :correct correct
     :line    card-line}))

(defn safe-inc
  [n]
  (if (nil? n) 1 (inc n)))

(defn duplicate-cards
  [counter n amt]
  (if (>= 0 amt)
    counter
    (let [copy-start         (inc n)
          copy-end           (+ copy-start amt)
          copy-range         (range copy-start copy-end)
          copies-of-cur-card (get counter n 1)]
      (reduce
        (fn [counter' _iter]
          (reduce (fn [acc n'] (update acc n' safe-inc))
            counter' copy-range))
        counter
        (range 0 copies-of-cur-card)
        ))))

(defn tick
  [state card-line]
  (let [{:keys [n amt] :as card} (line->card card-line)]
    (-> state
      (assoc-in [:cards n] card)
      (update-in [:counter n] safe-inc) ;; each tick is a new card
      (update :counter duplicate-cards n amt)
      )))

(defn discard-last-counter
  [{:keys [cards counter] :as state}]
  (let [valid-cards   (set (keys cards))
        counted-cards (set (keys counter))
        invalid-keys  (cset/difference counted-cards valid-cards)]
    (apply update state :counter dissoc invalid-keys)))

(defn sum
  [{:keys [counter]}]
  (apply + (vals counter)))

(->> lines
  (reduce tick {:cards {} :counter {}})
  discard-last-counter
  (tap :res)
  sum
  )
