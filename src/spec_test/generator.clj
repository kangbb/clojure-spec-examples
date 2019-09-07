(ns spec-test.generator
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

;; use predicate
(gen/sample (s/gen int?))
;; (0 0 1 0 -2 -16 -5 -37 1 -1)

;; use spec
(gen/sample (s/gen (s/cat :k keyword? :ns (s/+ number?))) 5)
;;=> ((:D -2.0)
;;=>  (:q4/c 0.75 -1)
;;=>  (:*!3/? 0)
;;=>  (:+k_?.p*K.*o!d/*V -3)
;;=>  (:i -1 -1 0.5 -0.5 -4))

;; Use exercise
(s/exercise (s/or :k keyword? :s string? :n number?) 5)
;;=> ([:H [:k :H]]
;;    [:ka [:k :ka]]
;;    [-1 [:n -1]]
;;    ["" [:s ""]]
;;    [-3.0 [:n -3.0]])

(gen/generate (s/gen even?))
;; Execution error (ExceptionInfo) at user/eval1281 (REPL:1).
;; Unable to construct gen at: [] for: clojure.core$even_QMARK_@73ab3aac

(gen/generate (s/gen (s/and int? even?)))
;;=> -15161796

(defn divisible-by [n] #(zero? (mod % n)))

(gen/sample (s/gen (s/and int?
                          #(> % 0)
                          (divisible-by 3))))
;;=> (3 9 1524 3 1836 6 3 3 927 15027)

;; Custom generator

;; simple but invalid
(s/def ::kws (s/and keyword? #(= (namespace %) "my.domain")))
(s/valid? ::kws :my.domain/name) ;; true
(gen/sample (s/gen ::kws)) ;; unlikely we'll generate useful keywords this way

;;; improve it - The first way
(def kw-gen (s/gen #{:my.domain/name :my.domain/occupation :my.domain/id}))
(gen/sample kw-gen 5)
;;=> (:my.domain/occupation :my.domain/occupation :my.domain/name :my.domain/id :my.domain/name)
(s/def ::kws (s/with-gen (s/and keyword? #(= (namespace %) "my.domain"))
                         #(s/gen #{:my.domain/name :my.domain/occupation :my.domain/id})))
;; with-gen takes a spec and a generator-returning function. Then return a spec.
(s/valid? ::kws :my.domain/name)  ;; true
(gen/sample (s/gen ::kws))
;;=> (:my.domain/occupation :my.domain/occupation :my.domain/name  ...)


;;; improve it - The second way
;; fmap apply a fn to generator. Also, return a generator.
;; withe gen/sample, every sample from generator will pass to fn to handler, then return
(def kw-gen-2 (gen/fmap #(keyword "my.domain" %) (gen/string-alphanumeric)))
(gen/sample kw-gen-2 5)
;;=> (:my.domain/ :my.domain/ :my.domain/1 :my.domain/1O :my.domain/l9p2)
;; use such-that as a filter. return a generator.
(def kw-gen-3 (gen/fmap #(keyword "my.domain" %)
                        (gen/such-that #(not= % "")
                                       (gen/string-alphanumeric))))
(gen/sample kw-gen-3 5)
;;=> (:my.domain/O :my.domain/b :my.domain/ZH :my.domain/31 :my.domain/U)

;;;Finally result for hello example
(s/def ::hello
  (s/with-gen #(clojure.string/includes? % "hello")
              #(gen/fmap (fn [[s1 s2]] (str s1 "hello" s2))
                         (gen/tuple (gen/string-alphanumeric) (gen/string-alphanumeric)))))
(gen/sample (s/gen ::hello))
;;=> ("hello" "ehello3" "eShelloO1" "vhello31p" "hello" "1Xhellow" "S5bhello" "aRejhellorAJ7Yj" "3hellowPMDOgv7" "UhelloIx9E")
