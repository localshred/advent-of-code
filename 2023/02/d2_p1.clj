(ns d2-p1
  (:require
   [clojure.string :as string]))

(defn tap-> [x tag] (println {tag x}) x)

(defn tap->> [tag x] (println {tag x}) x)
(def tap tap->>)

(def puzzle-limits {"red" 12 "green" 13 "blue" 14})
(def tests
  [[1 "Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green"]
   [2 "Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue"]
   [0 "Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red"]
   [0 "Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red"]
   [5 "Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green"]])

(def lines (->> "input.txt"
             (slurp)
             (string/trim)
             (string/split-lines)))

(def draw-pattern #"(\d+) (red|green|blue)")
(def game-tag-pattern #"^Game (\d+): ")

(defn parse-game-draw
  [game draw-str]
  (let [[_ n color] (re-find draw-pattern draw-str)]
    (assoc game color (Integer/parseInt n))))

(defn parse-game-draws
  [draws]
  (as-> draws $
    (string/split $ #", ") ;; [color-draw1 color-draw2 ...]
    (reduce parse-game-draw {"red" 0 "green" 0 "blue" 0} $)))

(defn parse-game
  [line]
  (let [game-n (->> line (re-find game-tag-pattern) last Integer/parseInt)
        draws  (as-> line $
                 (string/replace $ game-tag-pattern "") ;; draws-str
                 (string/split $ #"; ") ;; [draw1 draw2 ...]
                 (map parse-game-draws $))]
    {:n     game-n
     :line  line
     :draws draws}))

(defn valid-game?
  [limits game]
  (->> (:draws game)
    (every?
      (fn [draw]
        (every?
          (fn [[k v]]
            (<= v (limits k)))
          draw)))))

(defn game->int
  [limits line]
  (let [game (parse-game line)]
    (if (valid-game? limits game) (:n game) 0)))

;; Run it all
(->>
  lines
  (map #(game->int puzzle-limits %1))
  (reduce + 0)
  (tap :result))

;; Run the tests
(for [[expected line] tests]
  (let [actual (game->int puzzle-limits line)]
    (when-not (= expected actual)
      (println (format "FAIL! Expected %s, got %s (%s)" expected actual line)))))
