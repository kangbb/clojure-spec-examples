(ns spec-test.compose-predicate
  (:require [clojure.spec.alpha :as s]))


(s/def ::big-even (s/and int? even? #(> % 1000)))
(s/conform ::big-even :foo) ;; :clojure.spec.alpha/invalid
(s/conform ::big-even 10) ;; :clojure.spec.alpha/invalid
(s/conform ::big-even 100000) ;; true

;;; return a destruct data structure - a map entry.
(s/def ::name-or-id (s/or :name string?
                          :id   int?))
(s/conform ::name-or-id "abc") ;; [:name "abc"]
(s/conform ::name-or-id 100) ;; [:id 100]
(s/conform ::name-or-id :foo) ;; :clojure.spec.alpha/invalid