(ns spec-test.regx-spec
  (:require [clojure.spec.alpha :as s]
            [clojure.test.check.generators :as gen]))

(s/def ::config (s/*
                  (s/cat :prop string?
                         :val  (s/alt :s string? :b boolean?))))
(s/conform ::config ["-server" "foo" "-verbose" true "-user" "joe"])
;;=> [{:prop "-server", :val [:s "foo"]}
;;    {:prop "-verbose", :val [:b true]}
;;    {:prop "-user", :val [:s "joe"]}]

;; s/& which is the same as and
(s/def ::even-strings (s/& (s/* string?) #(even? (count %))))
(s/valid? ::even-strings ["a"])  ;; false
(s/valid? ::even-strings ["a" "b"])  ;; true
(s/valid? ::even-strings ["a" "b" "c"])  ;; false
(s/valid? ::even-strings ["a" "b" "c" "d"])  ;; true