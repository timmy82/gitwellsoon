(ns gitwellsoon.local
  (:require [clojure.string        :as str]
            [clj-jgit.porcelain    :as jg]
            [clj-jgit.querying     :as qg]
            [clojure.core.async    :as async :refer [go chan <!! <! >!! >! close! thread]]))

(def time-format "yyyy/MM/dd HH:mm:ss")

(defn- parse-time [time]
  (.format (java.text.SimpleDateFormat. time-format) time))

(defn- log-entry-from-change [author time message rev changed-file]
  "creates a change entry for a changed file from AUTHOR TIME MESSAGE REV CHANGED-FILE"
  (let [result {:author author
                :time (parse-time time)
                :entity (first changed-file)
                :loc-added 0
                :loc-deleted 0
                :message message
                :rev rev}] 
    result))

(defn- log-entries [log-entry]
  "takes a log-entry and for each changed file a log entry is created."
  (let [{author :author
         time :time
         message :message 
         rev :id
         changes :changed_files} log-entry]
    (map #(log-entry-from-change author time message rev %) changes)))

(defn- to-log-entries [git-log]
  "transforms a GIT-LOG into a list of entries."
  (flatten (vector (map #(log-entries %) git-log))))

(defn- read-git-log [path]
  "reads the git log of the repository located at path."
  (jg/with-repo path (map #(qg/commit-info repo %) (jg/git-log repo))))

(defn get-git-log [project]
  "reads the git log of the repository. Project is a map having keys name and a path."
  (let [{name :name 
         path :path} project]
    {:name name :log (-> (read-git-log path)
                         (to-log-entries))}))

(defn get-git-logs [projects]
  "reads the git log of each project contained in projects."
  (map #(get-git-log %) projects))

(defn get-git-log-async [project]
  "creates a go routine that writes the log into a channel and returns it."
  (let [git-log-channel (chan)]
    (go (>! git-log-channel (get-git-log project))
        (close! git-log-channel))
    git-log-channel))

(defn get-git-logs-async [projects]
  "creates a go routine for each project and retruns a sequence of channels."
;;  (reduce (fn [v project] (get-git-log-async project)) projects)
  (into [] (map #(get-git-log-async %) projects)))
