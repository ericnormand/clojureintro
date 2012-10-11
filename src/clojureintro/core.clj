(ns clojureintro.core)







1
2
3.0
"abc"
+
'+

















(+ 10 20)
(* 30 90)
(* (- 9 6) 8)




























(def x 22)
(+ x x)
(def h (* 9 x))




























(let [a 1
      b 2
      c 3]
  (+ a b c))
































(defn fib [n]
  (if (< n 2)
    1
    (+ (fib (dec n))
       (fib (- n 2)))))

(fib 0)
(fib 1)
(fib 10)





























;; list
;; accessed in order
;; append on the left is O(1)
(def l (list 1 "abc" :c 'c))

(nth l 2)
(seq l)
(first l)
(rest l)
(count l)
(conj l 1)












;; vector
;; random access by integer
;; append on the right is O(1)
(def v [12 42 "abc" + *])

(v 0)  ;; clojure.lang.IFn -- interface for functions
(v 1)
(get v 1) ;; java.util.Map
(nth v 1) ;; works for java.util.List, String, Array
(first v)
(rest v)
(conj v)





















;; hash map
;; no order
;; random access by key is O(1)
;; associate a new value is O(1)
(def m {1     "one"
        2     "two"
        "abc" 123
        :x    'hhh
        [1 3] "a vector"})

(m 2)  ;; IFn
(:x m) ;; Keywords are also IFn
(m [1 2] "not found")
(get m [1 3])
(first m)
(seq m)
;; nth not supported
(assoc m :newkey (fib 20))
(count m)
(conj m [0 :zero])































;; set
;; no order
;; unique elements
;; membership test O(1)
;; add new O(1)
(def s #{1 2 3})

(s 3)
(s "abc")
(seq s)
(conj s 400)
(conj s 3)
(count s)
















;; functional programming
(defn my-map [f c]
  (if (empty? c)
    c
    (cons (f (first c)) (rest c))))

(my-map inc [1 2 3 4 5])
(my-map (fn [x] (repeat x :hello)) [1 2 3 4 5])

(defn my-filter [p c]
  (if (p (first c))
    (cons (first c) (filter p (rest c)))
    (filter p (rest c))))

(my-filter even? [1 2 3 4 5])
(my-filter (fn [x] (> (count x) 0)) ["" "abc" "some string"])

(defn my-every? [p c]
  (or (empty? c)
      (and (p (first c))
           (my-every? p (rest c)))))

;; Imagine we have a list of lines that we would like to clean up
;; We don't want blank lines
;; We only want lines that are all digits
;; Then we read it in as an int

(defn cleanup [lines]
  (map (fn [s] (Integer/parseInt s))
       (filter (fn [s] (every? (fn [c] (Character/isDigit c)) s))
               (filter not-empty
                       (map (fn [s] (.trim s)) lines)))))





(defn cleanup2 [lines]
  (->> lines
       (map (fn [s] (.trim s)))
       (filter not-empty)
       (filter (fn [s] (every? (fn [c] (Character/isDigit c)) s)))
       (map (fn [s] (Integer/parseInt s)))))





(require '[clojure.string :as string])

(defn digit? [c]
  (Character/isDigit c))

(defn cleanup3 [lines]
  (->> lines
       (map string/trim)
       (filter not-empty)
       (filter #(every? digit? %))
       (map read-string)))




















;; ->> is a macro. What does it do?

(->> x)                 ;=> x
(->> x f)               ;=> (f x)
(->> x (f))             ;=> (f x)
(->> x (f a b))         ;=> (f a b x)
(->> x (f a b) (g 1 2)) ;=> (g 1 2 (f a b x))

;; we also have -> What does it do?

(-> x)                 ;=> x
(-> x f)               ;=> (f x)
(-> x (f))             ;=> (f x)
(-> x (f a b))         ;=> (f x a b)
(-> x (f a b) (g 1 2)) ;=> (g (f x a b) 1 2)















(with-open [in (BufferedInputStream. (FileReader. "abc"))]
  (.readLine in))

;; macroexpansion =>

(let [in (BufferedReader. (FileReader. "abc"))]
  (try
    (.readLine in)
    (finally
     (.close in))))














;; Classic bank account problem
;; I have two accounts and I want to transfer money between them

(define checking (ref 1000)) ;; $1000 in checking
(define savings  (ref 100))  ;; $100 in savings

;; I do not want to be able to read an inconsistent state
@checking => 1000
;; some other thread transfers $20
@savings  => 80
;; total: 1080, which is wrong

(dosync
 (+ @checking @savings)) => 1100

;; transfering is easy
(defn transfer [src dest amount]
  (dosync
   (alter src  - amount)
   (alter dest + amount)))

