(ns gitwellsoon.core
  (:require [gitwellsoon.code-maat :as cm]
            [gitwellsoon.github    :as gh]
            [gitwellsoon.local     :as gl]
            [clojure.core.async    :as a :refer [<!!]]))

(defn get-coupling [data]
  (cm/run-analysis :coupling data))

(defn get-authors [data]
  (cm/run-analysis :authors data))

(defn get-data-for [source user repo]
  (<!! (a/into [] (gh/download-changelist source user repo))))

(defn github-connector [opts]
  (gh/github opts))

(defn get-data-async [source user repo]
  (gh/download-changelist source user repo))

(defn get-local-git-log [path]
  (gl/get-git-log path))
