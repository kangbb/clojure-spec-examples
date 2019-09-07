(ns spec-test.usage-for-fn
  (:require [clojure.spec.gen.alpha :as gen]
            [clojure.spec.alpha :as s]
            [clojure.test :as t]
            [clojure.spec.test.alpha :as stest]
            [spec-test.compose-spec]))

;;;-- Runtime check to define a fn spec -------------------------------------------------
(defn person-name
  [person]
  ;; :pre [expr] expr is false, then throw an exception; if not, exec fn
  ;; :post [expr] expr is false, then throw an exception; if not, return value or end the fn.
  {:pre [(s/valid? :unq/person person)]
   :post [(s/valid? string? %)]}
  (str (:first-name person) " " (:last-name person)))

;; try-catch-finally can't capture the exception from :pre :post
;; so, you need to do some custom error process in :pre and :post, such as
;(defn person-name
;  [person]
;  ;; is [expr]  is expr is false, println fail info and return false; else return true.
;  {:pre [(t/is (s/valid? :unq/person person))]
;   :post [(t/is (s/valid? string? %))]}
;  (str (:first-name person) " " (:last-name person)))
;;
;; also can
;(defn person-name
;  [person]
;  {:pre [(try (s/valid? :unq/person person)
;              (catch Exception e
;                (println (.getMessage e))
;                false)
;              true)]
;   :post [(try (s/valid? string? %)
;               (catch Exception e
;                 (println (.getMessage e))
;                 false)
;               true)]}
;  (str (:first-name person) " " (:last-name person)))

;(person-name 42)
;;=> java.lang.AssertionError: Assert failed: (s/valid? :my.domain/person person)

(println (person-name {:first-name "Bugs" :last-name "Bunny" :email "bugs@example.com"}))
;; Bugs Bunny


(defn person-name-assert
  [person]
  ;; s/assert fail, throw an Exception; success, return the value.
  (let [p (s/assert ::person person)]
    (str (::first-name p) " " (::last-name p))))

;; open the assert functionality
(s/check-asserts true)
(try (person-name-assert 42)
     (catch Exception e (println (.getMessage e))))


;;;--Use spec tool to define fn spec validate-------------------------------------------------
;; Normal function
(defn ranged-rand
  "Returns random int in range start <= rand < end"
  [start end]
  (+ start (long (rand (- end start)))))
;; :args   spec for args
;; :ret    spec for return val
;; :fn     validate the relation between args and ret-val
(s/fdef ranged-rand
        :args (s/and (s/cat :start int? :end int?)
                     #(< (:start %) (:end %)))
        :ret int?
        :fn (s/and #(>= (:ret %) (-> % :args :start))
                   #(< (:ret %) (-> % :args :end))))

;; High order function, use fspec
(defn adder [x] #(+ x %))
(s/fdef adder
        :args (s/cat :x number?)
        :ret (s/fspec :args (s/cat :y number?)
                      :ret number?)
        :fn #(= (-> % :args :x) ((:ret %) 0)))

;; open test for args
(stest/instrument `ranged-rand)
(try (ranged-rand 8 7)
     (catch Exception e (println (.getMessage e))))


;;;-- Use generator to do checking-------------------------------------------------------------
; (stest/check `ranged-rand) ; return a lazy coll
(println (stest/check `ranged-rand))                        ; the function have overflow exception
;;=> ({:spec #object[clojure.spec.alpha$fspec_impl$reify__13728 ...],
;;     :clojure.spec.test.check/ret {:result true, :num-tests 1000, :seed 1466805740290},
;;     :sym spec.examples.guide/ranged-rand,
;;     :result true})

;; to get an abbreviated result
(println (stest/abbrev-result (first (stest/check `ranged-rand))))
;; to get a summarize result
(println (stest/summarize-results (stest/check `ranged-rand)))

; (s/exercise-fn `ranged-rand)
(println (s/exercise-fn `ranged-rand))
;;=> ([(-1 0) -1] [(-1 0) -1] [(-22 6) -15]
;;    [(-22 -1) -9] [(-3 -1) -3] [(-12 -10) -12]
;;    [(-8 12) -5] [(-226 -37) -100] [(-85 50) -45]
;;    [(-1 53) 6])

;;;-- More------------------------------------------------------------------------------------
;; Use instrument :stub to generator return value for a function.
;; The return value generated is according to :ret spec
;; Do this, can mock a function.
(defn invoke-service [service request]
  ;; invokes remote service
  )

(defn run-query [service query]
  (let [{::keys [result error]} (invoke-service service {::query query})]
    (or result error)))

(s/def ::query string?)
(s/def ::request (s/keys :req [::query]))
(s/def ::result (s/coll-of string? :gen-max 3))
(s/def ::error int?)
(s/def ::response (s/or :ok (s/keys :req [::result])
                        :err (s/keys :req [::error])))

(s/fdef invoke-service
        :args (s/cat :service any? :request ::request)
        :ret ::response)

(s/fdef run-query
        :args (s/cat :service any? :query string?)
        :ret (s/or :ok ::result :err ::error))

(stest/instrument `invoke-service {:stub #{`invoke-service}})
(stest/summarize-results (stest/check `run-query))
;;=> {:total 1, :check-passed 1}


;;; variadic function

(defn such-arity
  ([] "nullary")
  ([one] "unary")
  ([one two & many] "one two many"))

(s/fdef such-arity
        :args (s/alt :nullary (s/cat)
                     :unary (s/cat :one any?)
                     :variadic (s/cat :one any?
                                      :two any?
                                      :many (s/* any?))))
