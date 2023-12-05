(ns d5-p1
  (:require
   [clojure.data :refer [diff]]
   [clojure.pprint :refer [pprint] :rename {pprint pp}]
   [clojure.string :as string]))

(defn tap-> [x tag] (pp {tag x}) x)
(defn tap->> [tag x] (pp {tag x}) x)
(def tap tap->>)

(def input (->> "input.txt"
             (slurp)
             (string/trim)))

(def tests
  "seeds: 79 14 55 13

seed-to-soil map:
50 98 2
52 50 48

soil-to-fertilizer map:
0 15 37
37 52 2
39 0 15

fertilizer-to-water map:
49 53 8
0 11 42
42 0 7
57 7 4

water-to-light map:
88 18 7
18 25 70

light-to-temperature map:
45 77 23
81 45 19
68 64 13

temperature-to-humidity map:
0 69 1
1 0 69

humidity-to-location map:
60 56 37
56 93 4")

(defn line->nums
  [line]
  (as-> line $
    (string/split $ #"\s+")
    (map #(BigInteger. %1) $)))

(def patterns
  {:seeds                   #"seeds: ([\d \n]+)\n"
   :seed-to-soil            #"seed-to-soil map:\n([\d \n]+)\n"
   :soil-to-fertilizer      #"soil-to-fertilizer map:\n([\d \n]+)\n"
   :fertilizer-to-water     #"fertilizer-to-water map:\n([\d \n]+)\n"
   :water-to-light          #"water-to-light map:\n([\d \n]+)\n"
   :light-to-temperature    #"light-to-temperature map:\n([\d \n]+)\n"
   :temperature-to-humidity #"temperature-to-humidity map:\n([\d \n]+)\n"
   :humidity-to-location    #"humidity-to-location map:\n([\d \n]+)\n"})

(def traverse-path
  [:seed-to-soil
   :soil-to-fertilizer
   :fertilizer-to-water
   :water-to-light
   :light-to-temperature
   :temperature-to-humidity
   :humidity-to-location])

(defn input-find
  [pattern-key input]
  (->> input
    (re-find (get patterns pattern-key))
    second
    string/split-lines
    (map line->nums)))

(defn input->mappings
  [input]
  (as-> {:seeds->locations {}} $
    (assoc $ :seeds (-> (input-find :seeds input) first))
    (reduce (fn [state k] (assoc state k (input-find k input)))
      $ traverse-path)))

(defn in-mapping?
  [n start offset]
  (and
    (<= start n)
    (< n (+ start offset))))

(defn source->destination
  [state mapping-k source-at]
  (if-let [[dest-start source-start] (->> (get state mapping-k)
                                       (some (fn [[dest-start source-start offset]]
                                               (when (in-mapping? source-at source-start offset)
                                                 [dest-start source-start]))))]
    (+
      (- source-at source-start)
      dest-start)
    source-at))

(defn seeds->locations
  [{:keys [seeds] :as state}]
  (reduce (fn [state' seed-at]
            (let [location-at (reduce (fn [source-at mapping-k]
                                        (source->destination state' mapping-k source-at))
                                   seed-at
                                   traverse-path)]
              (update state' :seeds->locations assoc seed-at location-at)))
    state
    seeds))

(defn nearest-seed-location
  [{:keys [seeds->locations]}]
  (apply min (vals seeds->locations)))

(-> input
  input->mappings
  seeds->locations
  nearest-seed-location
  )


(let [expected {:seeds                   [79 14 55 13]
                :seed-to-soil            [{:range (range 98 (+ 98 2)) :dest-start 50 :offset 2}    ;; 50 98 2
                                          {:range (range 50 (+ 50 48)) :dest-start 52 :offset 48}] ;; 52 50 48
                :soil-to-fertilizer      [{:range (range 15 (+ 15 37)) :dest-start 0 :offset 37}   ;; 0 15 37
                                          {:range (range 52 (+ 52 2)) :dest-start 37 :offset 2}    ;; 37 52 2
                                          {:range (range 0 (+ 0 15)) :dest-start 39 :offset 15}]   ;; 39 0 15
                :fertilizer-to-water     [{:range (range 53 (+ 53 8)) :dest-start 49 :offset 8}    ;; 49 53 8
                                          {:range (range 11 (+ 11 42)) :dest-start 11 :offset 42}  ;; 0 11 42
                                          {:range (range 0 (+ 0 7)) :dest-start 42 :offset 7}      ;; 42 0 7
                                          {:range (range 7 (+ 7 4)) :dest-start 57 :offset 4}]     ;; 57 7 4
                :water-to-light          [{:range (range 18 (+ 18 7)) :dest-start 88 :offset 7}    ;; 88 18 7
                                          {:range (range 25 (+ 25 70)) :dest-start 18 :offset 70}] ;; 18 25 70
                :light-to-temperature    [{:range (range 77 (+ 77 23)) :dest-start 45 :offset 23}  ;; 45 77 23
                                          {:range (range 45 (+ 45 19)) :dest-start 81 :offset 19}  ;; 81 45 19
                                          {:range (range 64 (+ 64 13)) :dest-start 68 :offset 13}] ;; 68 64 13
                :temperature-to-humidity [{:range (range 69 (+ 69 1)) :dest-start 0 :offset 1}     ;; 0 69 1
                                          {:range (range 0 (+ 0 69)) :dest-start 1 :offset 69}]    ;; 1 0 69
                :humidity-to-location    [{:range (range 56 (+ 56 37)) :dest-start 60 :offset 37}  ;; 60 56 37
                                          {:range (range 93 (+ 93 4)) :dest-start 56 :offset 4}]}  ;; 56 93 4
      actual         (input->mappings tests)
      [left right _] (diff expected actual)]
      (cond
        (and (empty? left) (empty? right))
        {:msg "Tests passed"}
        ;;
        (and (empty? left) (seq right))
        {:msg          "FAIL: Result contained unexpected values"
         :expected     expected
         :actual       actual
         :extra-values left}
        ;;
        (and (seq left) (empty? right))
        {:msg            "FAIL: Result missing some expected values"
         :expected       expected
         :actual         actual
         :missing-values right}
        ;;
        :else
        {:msg            "FAIL: Result missing some expected values, contains unexpected values"
         :expected       expected
         :actual         actual
         :missing-values left
         :extra-values   right}
        ))
