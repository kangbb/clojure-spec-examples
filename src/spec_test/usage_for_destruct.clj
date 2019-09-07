(ns spec-test.usage-for-destruct
  (:require [clojure.spec.alpha :as s]
            [spec-test.regx-spec :as rs]))

;; Use conform to implement

(defn- set-config [prop val]
  ;; dummy fn
  (println "set" prop val))

(defn configure [input]
  (let [parsed (s/conform ::rs/config input)]
    (if (= parsed ::s/invalid)
      (throw (ex-info "Invalid input" (s/explain-data ::rs/config input)))
      (for [{prop :prop [_ val] :val} parsed]
        (set-config (subs prop 1) val)))))

(configure ["-server" "foo" "-verbose" true "-user" "joe"])
