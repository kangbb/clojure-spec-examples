(ns spec-test.compose-spec
  (:require [clojure.spec.alpha :as s]))

(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")
(s/def ::email-type (s/and string? #(re-matches email-regex %)))

(s/def ::acctid int?)
(s/def ::first-name string?)
(s/def ::last-name string?)
(s/def ::email ::email-type)

(s/def ::person (s/keys :req [::first-name ::last-name ::email]
                        :opt [::phone]))

(s/def :unq/person
  (s/keys :req-un [::first-name ::last-name ::email]
          :opt-un [::phone]))

(s/explain ::person
          {::first-name :name
           ::last-name "Bunny"
           ::email "bugs@example.com"})

(s/explain :unq/person
           {:first-name :name
            :last-name "Bunny"
            :email "bugs@example.com"})