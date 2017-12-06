'{:dependencies [[org.clojure/clojure "1.8.0"]]}

(defn tap [x] (do (println x) x))

(def input (->> "input.txt"
               (slurp)
               (clojure.string/trim)
               (seq)
               (map #(Integer/parseInt (str %)))))

(def middleIndex (/ (count input) 2))

(defn pairNth
  [index]
  (cond
    (< index middleIndex) (+ index middleIndex)
    (> index middleIndex) (- index middleIndex)
    :else 0))

(defn push
  [value values]
  (conj values value))

(defn reducer
  [nums index]
  (let [current (nth input index)
        pairIndex (pairNth index)
        pair (nth input pairIndex)
        same? (= current pair)]
    (if same? (conj nums current) nums)))

(def result (->> input
                 (count)
                 (range)
                 (reduce reducer [])
                 (tap)
                 (reduce + 0)))

(println result)
