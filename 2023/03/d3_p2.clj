(ns d3-p2
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
(def gear-symbol? #{\*})
(def part-symbol?
  (fn [c] (and (not-digit? c) (not= \_ c) (not= \. c))))

(def init-digit
  {:n nil :row nil :start nil :end nil})

(defn init-state
  [cols]
  {:c                    nil
   :col                  0
   :cols                 cols
   :digit                init-digit
   :digits               []
   :digits-m             {}
   :gear-symbols         #{}
   :valid-gear-symbols   #{}
   :invalid-gear-symbols #{}
   :in-digit?            false
   :m                    {}
   :row                  0
   :sum                  0})

(defn c-kind
  [c]
  (cond
    (digit? c)       :digit
    (gear-symbol? c) :gear-symbol
    (part-symbol? c) :part-symbol
    :else            :other))

(defn store-digit
  [{:keys [digit] :as state}]
  (let [{:keys [row start end]} digit
        xy-pairs                (->> (range start (inc end))
                                  (map (fn [y] [[row y] digit])))]
    (-> state
     (update :digits conj digit)
     (assoc :digit init-digit) ;; reset the digit
     (update :digits-m #(reduce (fn [m [k v]] (assoc m k v)) %1 xy-pairs)) ;; reference digit for each xy
     )))

(defn last-col?
  [{:keys [col cols]}]
  (= col (dec cols)))

(defn tick-selector
  [{:keys [c ck in-digit?] :as state}]
  (if in-digit?
    (cond
      (and (= :digit ck) (last-col? state)) :digit-append-and-finalize
      (= :digit ck)                         :digit-append
      :else                                 :digit-finalize)
    (cond
      (= :digit ck)    :digit-start
      (gear-symbol? c) :gear-symbol
      :else            :skip)))

(defmulti tick #'tick-selector)

(defmethod tick :default
  [state]
  (throw
    (ex-info "tick: unknown digit state..."
      {:state state})))

(defmethod tick :skip
  [state]
  state)

(defn record-gear-symbol-location
  [{:keys [ck row col] :as state}]
  (if (= ck :gear-symbol)
    (-> state
     (update :gear-symbols conj [row col]))
    state))

(defmethod tick :gear-symbol
  [state]
  (record-gear-symbol-location state))

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
     (record-gear-symbol-location)
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

(defn zip-range
  [row start end]
  (let [xy-range    (range start end)
        row-repeats (repeat (count xy-range) row)]
    (map list row-repeats xy-range)))

(defn above-row-range
  [col-count [row col]]
  (when (> row 0)
    (let [above-row (dec row)
          start     (max 0 (dec col))
          end       (min col-count (+ col 2))]
      (zip-range above-row start end))))

(defn below-row-range
  [row-count col-count [row col]]
  (let [last-row (dec row-count)]
    (when (< row last-row)
      (let [below-row (inc row)
            start     (max 0 (dec col))
            end       (min col-count (+ col 2))]
       (zip-range below-row start end)))))

(defn same-row-left-range
  [[row col]]
  (when (> col 0)
    (list (list row (dec col)))))

(defn same-row-right-range
  [col-count [row col]]
  (let [last-col (dec col-count)]
    (when (< col last-col)
      (list (list row (inc col))))))

(defn digit-ranges
  [{:keys [cols row]} symbol-pos]
  (->> [(above-row-range cols symbol-pos)
        (below-row-range row cols symbol-pos)
        (same-row-left-range symbol-pos)
        (same-row-right-range cols symbol-pos)]
    (remove nil?)
    (reduce concat)))

(defn gear-symbol->digits
  [{:keys [digits-m] :as state} symbol-pos]
  (let [adjacent-xys (digit-ranges state symbol-pos)]
    (reduce (fn [digits symbol-pos]
              (if-let [digit (get digits-m symbol-pos)]
                (conj digits digit)
                digits))
      #{}
      adjacent-xys)))

(defn sum-gear-digits
  [{:keys [gear-symbols] :as state}]
  (reduce (fn [state' symbol-pos]
            (if-let [digits (gear-symbol->digits state' symbol-pos)]
              (if (= 2 (count digits))
                (let [[a b]   (vec digits)
                      a       (Integer/parseInt (:n a))
                      b       (Integer/parseInt (:n b))
                      product (* a b)]
                  (-> state'
                   (update :sum + product) ;; update the sum
                   (update :valid-gear-symbols conj symbol-pos)
                   ))
                (-> state'
                  (update :invalid-gear-symbols conj symbol-pos)))
              (-> state'
                  (update :invalid-gear-symbols conj symbol-pos))))
    state
    gear-symbols))

(def tests
  "Sum should be 467835"
  (string/split-lines
"467____114
___*______
__35__633_
______#___
617*______
_____+_58_
__592_____
______755_
___$_*____
_664_598__"))

(as-> lines $
    (puzzle->matrix $)
    (sum-gear-digits $)
    (dissoc $ :m :digits)
    )
