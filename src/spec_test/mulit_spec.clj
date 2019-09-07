(ns spec-test.mulit-spec
  (:require [clojure.spec.alpha :as s]
            [clojure.test.check.generators :as gen]))
(s/def :event/type keyword?)
(s/def :event/timestamp int?)
(s/def :search/url string?)
(s/def :error/message string?)
(s/def :error/code int?)

(defmulti event-type :event/type)
(defmethod event-type :event/search [_]
  (s/keys :req [:event/type :event/timestamp :search/url]))
(defmethod event-type :event/error [_]
  (s/keys :req [:event/type :event/timestamp :error/message :error/code]))

(s/def :event/event (s/multi-spec event-type :event/type))

(println (gen/sample (s/gen :event/event)))

(s/valid? :event/event
          {:type :search
           :event/timestamp 1463970123000
           :search/url "https://clojure.org"})
;=> true
(s/valid? :event/event
          {:type :error
           :event/timestamp 1463970123000
           :error/message "Invalid host"
           :error/code 500})
;=> true
