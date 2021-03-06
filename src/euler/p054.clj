(ns euler.p054
  (:require [clojure.string :as str]
            [clojure.math.numeric-tower :as math]))

(def input-file "resources/p054-input.txt")

(defn slurp-n-split [file]
  (map #(str/split % #" ") (str/split-lines (slurp file))))

(def rank-replacement { \T "10", \J "11", \Q "12", \K "13", \A "14"})
(def suit-replacement { \H :H, \C :C, \S :S, \D :D})

(defn transform-card [card]
  (let [[rank suit] (seq card)]
    [(-> (rank-replacement rank rank) str read-string dec) (suit-replacement suit suit)]))

(def transformed-input (map #(map transform-card %) (slurp-n-split input-file)))

(def base 13)
(def size-hand 5)
(def weights (vec (map #(math/expt base %) (range 0 20))))

(defn sortering [ranks]
  (flatten (sort-by count (partition-by identity (sort ranks)))))

(defn rank-score [ranks]
  (reduce + (map * (sortering ranks) weights)))

(def high-card-hand [[1 :D] [2 :H] [7 :S] [10 :H] [4 :S] ])
(def pair-hand [[1 :H] [2 :H] [7 :H] [4 :H] [4 :S] ])
(def two-pair-hand [[2 :H] [2 :D] [7 :H] [4 :H] [4 :S] ])
(def three-kind-hand [ [2 :H] [4 :D] [7 :H] [4 :H] [4 :S] ])
(def straight-hand [ [4 :H] [5 :S] [6 :D] [7 :H] [8 :S] ])
(def flush-hand [[1 :H] [2 :H] [3 :H] [4 :H] [10 :H] ])
(def house-hand [[3 :H] [2 :H] [3 :S] [3 :D] [2 :S] ])
(def four-kind-hand [[3 :H] [3 :H] [3 :H] [3 :H] [2 :S] ])
(def straight-flush-hand [[1 :H] [2 :H] [3 :H] [4 :H] [5 :H] ])

(defn find-frequencies [values]
  (-> values frequencies vals sort reverse))

(def ranks-def [ [1]  [2]  [2 2] [3]  [1   ] [1]  [3 2] [4]  [1] ])
(def suits-def [ [ ]  [ ]  [   ] [ ]  [    ] [5]  [   ] [ ]  [5] ])
(def sum-def   [ true true true  true false  true true  true false])

(defn check-sizes [definition actual]
  (= (take (count definition) actual) (-> definition sort reverse)))

(defn my-match [kind defi hand]
  (let [freq (find-frequencies (map kind hand))]
    (map #(check-sizes % freq) defi)))

(defn is-straight? [ranks]
  (= (- (reduce + ranks) (* (first (sort ranks)) 5)) 10))

(defn my-straight [def ranks]
  (let [yes (is-straight? ranks)]
    (map #(or yes %) def)))

(defn total-match [hand]
  (let [rank-m (my-match first ranks-def hand)
        suit-m (my-match second suits-def hand)
        straight (my-straight sum-def (map first hand))]
    (map #(and %1 %2 %3) rank-m suit-m straight)))

(defn get-score-index [hand]
  (apply max (keep-indexed #(if %2 %1 nil) (total-match hand))))

(defn get-score [hand] 
  (+ (weights (+ 5 (get-score-index hand))) (rank-score (map first hand))))

(defn one-wins? [hands] 
  (let [[one two] (partition 5 hands)]
       (> (get-score one) (get-score two))))

(defn count-one-wins [data]
  (count (filter true? (map one-wins? data))))

