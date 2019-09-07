(ns spec-test.usage-for-file
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]))

(-> (stest/enumerate-namespace 'user) stest/check)
