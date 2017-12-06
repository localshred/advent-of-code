'{:dependencies [[org.clojure/clojure "1.8.0"]]}

(defn tap [x] (do (println x) x))

(def input (->> "input.txt"
               (slurp)
               (clojure.string/trim)
               (seq)
               (map #(Integer/parseInt (str %)))))

(def initial {:nums [] :previous (last input)})
(println initial)

(defn push
  [value values]
  (conj values value))

(defn reducer
  [acc n]
  (let [previous (:previous acc)
        same? (= n previous)
        updated-acc (if same? (update-in acc [:nums] (partial push n)) acc)]
    (update-in updated-acc [:previous] (constantly n))))

(def result (->> input
                 (reduce reducer initial)
                 (:nums)
                 (tap)
                 (reduce + 0)))

(println result)
