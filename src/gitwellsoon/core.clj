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

(defn get-local-git-log [project]
  (gl/get-git-log project))

(defn get-local-git-logs [projects]
  (gl/get-git-logs projects))

(defn get-local-git-logs-async [projects]
  (<!! (a/into [] (gl/get-git-logs-async projects))))

