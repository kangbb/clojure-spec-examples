(ns spec-test.coll-spec
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(s/conform (s/coll-of keyword?) [:a :b :c])
(s/def ::scores (s/map-of (s/or :name string? :like int?) int? :conform-keys true))
(s/conform ::scores {10 1000, "Joe" 500})
;; => {"Sally" 1000, "Joe" 500}

(s/def ::point (s/tuple double? double? string?))
(s/conform ::point [1.5 2.5 -0.5])
;;=> :clojure.spec.alpha/invalid

(s/def ::person (s/every-kv keyword? string? ))
(gen/sample (s/gen ::person))

(s/def ::animal (s/every keyword?))
(gen/sample (s/gen ::animal))