(ns gitwellsoon.core
  (:require [gitwellsoon.code-maat :as cm]
            [gitwellsoon.github    :as gh]
            [clojure.core.async    :as a :refer [<!!]]))

(defn get-coupling [data]
  (cm/run-analysis :coupling data))

(defn get-authors [data]
  (cm/run-analysis :authors data))

(defn get-data-for [user repo]
  (<!! (a/into [] (gh/download-changelist gh/github user repo))))

(defn get-data-async [user repo]
  (gh/download-changelist gh/github user repo))
