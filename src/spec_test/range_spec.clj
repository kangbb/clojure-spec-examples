(ns spec-test.range-spec
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(s/def ::roll (s/int-in 0 11))
(s/valid? ::roll 3)
(gen/sample (s/gen ::roll))
;;=> (1 0 0 3 1 7 10 1 5 0)

(s/def ::the-aughts (s/inst-in #inst "2000" #inst "2010"))
(s/valid? ::the-aughts #inst"2005-03-03T08:40:05.393-00:00")
(drop 50 (gen/sample (s/gen ::the-aughts) 55))
;;=> (#inst"2005-03-03T08:40:05.393-00:00"
;;    #inst"2008-06-13T01:56:02.424-00:00"
;;    #inst"2000-01-01T00:00:00.610-00:00"
;;    #inst"2006-09-13T09:44:40.245-00:00"
;;    #inst"2000-01-02T10:18:42.219-00:00")

(s/def ::dubs (s/double-in :min -100.0 :max 100.0 :NaN? false :infinite? false))
(s/valid? ::dubs 2.9)
;;=> true
(s/valid? ::dubs Double/POSITIVE_INFINITY)
;;=> false
(gen/sample (s/gen ::dubs))
;;=> (-1.0 -1.0 -1.5 1.25 -0.5 -1.0 -3.125 -1.5625 1.25 -0.390625)
