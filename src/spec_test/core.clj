(ns spec-test.core
  (:require [clojure.core.async :as async :refer :all]
            ;[spec-test.basic]
            ;[spec-test.compose-predicate]
            ;[spec-test.compose-spec]
            ;[spec-test.mulit-spec]
            ;[spec-test.usage-for-fn]
            ;[spec-test.usage-for-destruct]
            [spec-test.usage-for-fn]))
;;; Using unqualified and qualified namespace spec
; (s/explain :animal/dog :name)
;(s/explain :spec-test.basic/suit :apple)


(defn -main [& args]
  (println "main start"))
