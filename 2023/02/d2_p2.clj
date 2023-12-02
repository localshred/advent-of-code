(ns d2-p2
  (:require
   [clojure.string :as string]))

(defn tap-> [x tag] (println {tag x}) x)

(defn tap->> [tag x] (println {tag x}) x)
(def tap tap->>)

(def tests
  [[48 "Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green"]
   [12 "Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue"]
   [1560 "Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red"]
   [630 "Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red"]
   [36 "Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green"]])

(def lines (->> "input.txt"
             (slurp)
             (string/trim)
             (string/split-lines)))

(def draw-pattern #"(\d+) (red|green|blue)")
(def game-tag-pattern #"^Game (\d+): ")

(def init-game {"red" 0 "green" 0 "blue" 0})

(defn parse-game-draw
  [game draw-str]
  (let [[_ n color] (re-find draw-pattern draw-str)]
    (assoc game color (Integer/parseInt n))))

(defn parse-game-draws
  [draws]
  (as-> draws $
    (string/split $ #", ") ;; [color-draw1 color-draw2 ...]
    (reduce parse-game-draw init-game $)))

(defn set-min
  [curr draw k]
  (max curr (draw k)))

(defn min-draws-reducer
  [acc draw]
  (-> acc
    (update "red" set-min draw "red")
    (update "green" set-min draw "green")
    (update "blue" set-min draw "blue")))

(defn parse-game
  [line]
  (let [draws     (as-> line $
                    (string/replace $ game-tag-pattern "") ;; draws-str
                    (string/split $ #"; ") ;; [draw1 draw2 ...]
                    (map parse-game-draws $))
        min-draws (reduce min-draws-reducer init-game draws)
        power     (->> min-draws vals (reduce *))]
    {:line      line
     :min-draws min-draws
     :power     power
     :draws     draws}))

;; Run it all
(->>
  lines
  (map (comp :power parse-game))
  (reduce + 0)
  (tap :result))

;; Run the tests
(for [[expected line] tests]
  (let [actual (parse-game line)]
    (if-not (= expected (:power actual))
      (println (format "FAIL! Expected %s, got %s (%s)" expected actual line))
      (println "PASS"))))
