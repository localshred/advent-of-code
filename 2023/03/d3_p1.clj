(ns d3-p1
  (:require
   [clojure.string :as string]))

(defn tap-> [x tag] (println {tag x}) x)
(defn tap->> [tag x] (println {tag x}) x)
(def tap tap->>)

(def lines (->> "input.txt"
             (slurp)
             (string/trim)
             (string/split-lines)))

(def digit? #{\1 \2 \3 \4 \5 \6 \7 \8 \9 \0})
(def not-digit? (complement digit?))
(def part-symbol?
  (fn [c] (and (not-digit? c) (not= \. c))))

(def init-digit
  {:n nil :row nil :start nil :end nil})

(defn init-state
  [cols]
  {:c           nil
   :col         0
   :cols        cols
   :digit       init-digit
   :digits      []
   :good-digits []
   :bad-digits  []
   :in-digit?   false
   :m           {}
   :row         0
   :sum         0})

(defn c-kind
  [c]
  (cond
    (digit? c)       :digit
    (part-symbol? c) :symbol
    :else            :other))

(defn store-digit
  [{:keys [digit] :as state}]
  (-> state
    (update :digits conj digit)
    (assoc :digit init-digit) ;; reset the digit
    ))

(defn last-col?
  [{:keys [col cols]}]
  (= col (dec cols)))

(defn tick-selector
  [{:keys [ck in-digit?] :as state}]
  (if in-digit?
    (cond
      (and (= :digit ck) (last-col? state)) :digit-append-and-finalize
      (and (= :digit ck)) :digit-append
      :else         :digit-finalize)
    (cond
      (= :other ck)  :skip
      (= :symbol ck) :symbol
      (= :digit ck)  :digit-start)))

(defmulti tick #'tick-selector)

(defmethod tick :default
  [state]
  (throw
    (ex-info "tick: unknown digit state..."
      {:state state})))

(defmethod tick :skip
  [state]
  state)

(defmethod tick :symbol
  [state]
  state)

(defmethod tick :digit-start
  [{:keys [c col row] :as state}]
  (-> state
    (assoc :in-digit? true)
    (assoc-in [:digit :row] row)
    (assoc-in [:digit :start] col)
    (assoc-in [:digit :n] (str c))))

(defn digit-finalize
  [{:keys [ck col] :as state}]
  (let [end (if (and (= ck :digit) (last-col? state))
              col
              (dec col))]
    (-> state
     (assoc-in [:digit :end] end)
     (assoc :in-digit? false)
     (store-digit)
     )))

(defn digit-append
  [{:keys [c] :as state}]
  (-> state
    (update-in [:digit :n] str c)))

(defmethod tick :digit-append
  [state]
  (digit-append state))

(defmethod tick :digit-finalize
  [state]
  (digit-finalize state))

(defmethod tick :digit-append-and-finalize
  [state]
  (-> state digit-append digit-finalize))

(defn puzzle->matrix
  [lines]
  (let [cols        (-> lines first count)
        init        (init-state cols)
        col-reducer (fn [{:keys [row col] :as state} c]
                      (-> state
                        (assoc :c c)
                        (assoc :ck (c-kind c))
                        (update :m assoc [row col] c) ;; assign char in matrix
                        (tick)
                        (update :col inc)             ;; inc the cur column num
                        ))
        row-reducer (fn [state row]
                      (as-> (seq row) $
                        (reduce col-reducer state $)
                        (update $ :row inc) ;; inc row
                        (assoc $ :col 0)    ;; reset column count at end of row
                        ))]
    (reduce row-reducer init lines)))

(defn above-row-range
  [col-count {:keys [row start end]}]
  (when (> row 0)
    (let [above-row (dec row)
          start    (max 0 (dec start))
          end      (min (dec col-count) (+ end 2))]
     [:above above-row (range start end)])))

(defn below-row-range
  [row-count col-count {:keys [row start end]}]
  (let [last-row (dec row-count)]
    (when (< row last-row)
     (let [below-row (inc row)
           start    (max 0 (dec start))
           end      (min (dec col-count) (+ end 2))]
       [:below below-row (range start end)]))))

(defn same-row-left-range
  [_ {:keys [row start]}]
  (when (> start 0)
    [:left row (range (dec start) start)]))

(defn same-row-right-range
  [col-count {:keys [row end]}]
  (let [last-col (dec col-count)]
    (when (< end last-col)
      [:right row (range (inc end) (+ end 2))])))

(defn digit-ranges
  [{:keys [cols row]} digit]
  (remove nil? [(above-row-range cols digit)
                (below-row-range row cols digit)
                (same-row-left-range cols digit)
                (same-row-right-range cols digit)]))

(defn part-touching-symbol?
  [state digit]
  (let [ranges (digit-ranges state digit)]
    (if (empty? ranges)
      false
      (->> ranges
        (some (fn [[_ row col-range]]
                (->> col-range
                  (some (fn [c]
                          (-> state
                            (get-in [:m [row c]])
                            part-symbol?))))))))))

(defn sum-valid-digits
  [{:keys [digits] :as state}]
  (reduce (fn [state' {:keys [n] :as digit}]
            (if (part-touching-symbol? state' digit)
              (-> state'
                (update :sum + (Integer/parseInt n)) ;; update the sum
                (update :good-digits conj digit)
                )
              (-> state'
                (update :bad-digits conj digit))))
    state
    digits))

(def tests
  (string/split-lines
"467....114
...*......
..35..633.
......#...
617*......
.....+.58.
..592.....
......755.
...$.*....
.664.598.."))

(as-> lines $
    (puzzle->matrix $)
    (sum-valid-digits $)
    (select-keys $ [:sum :good-digits :bad-digits])
    )
