(ns spec-test.basic
  (:require [clojure.spec.alpha :as s]))

;;; Simple usage
(s/conform even? 1000)
 ;;=> 1000
(s/valid? even? 1000)
;;=> true
(s/explain even? 1000)
;;=> Success

(s/valid? #(> % 5) 10) ;; true
(s/valid? #(> % 5) 0) ;; false

(s/valid? #{:club :diamond :heart :spade} :club) ;; true
(s/valid? #{:club :diamond :heart :spade} 42) ;;

;;; Registry
;; unqualified namespace
(s/def ::date inst?)
(s/def ::suit #{:club :diamond :heart :spade})

;; qualified namespace
(s/def :animal/dog #{:name :age})


;;; Use validate-function
(s/valid? ::date (java.util.Date.))
;;=> true
(s/valid? ::date 42)
;;=> false

(s/conform ::suit :club)
;;=> :club
(s/conform ::suit "like")
;;=> :clojure.spec.alpha/invalid

(s/explain ::date (java.util.Date.))
;;=> Success!
(s/explain ::date 42)
;;=> 42 - failed: inst? spec: :spec-test.core/date

;;; Use namespace qualified and unqualified
(s/explain ::suit :like)
(s/explain :spec-test.basic/suit :like)

(s/explain :animal/dog :apple)