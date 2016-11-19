(ns gitwellsoon.local
  (:require [clojure.string        :as str]
            [clj-jgit.porcelain    :as jg]
            [clj-jgit.querying     :as qg]
            [clojure.core.async    :as async :refer [go chan <!! <! >!! >! close! thread]]))

(def time-format "yyyy/MM/dd HH:mm:ss")

(defn parse-time [time]
  (.format (java.text.SimpleDateFormat. time-format) time))

(defn log-entry-from-change [author time message rev changed-file]
  "creates a change entry for a changed file from AUTHOR TIME MESSAGE REV CHANGED-FILE"
  (let [result {:author author
                :time (parse-time time)
                :entity (first changed-file)
                :loc-added 0
                :loc-deleted 0
                :message message
                :rev rev}] 
    result))

(defn log-entries [log-entry]
  "takes a GIT-LOG-ENTRY and for each changed file a log entry is created"
  (let [{author :author
         time :time
         message :message 
         rev :id
         changes :changed_files} log-entry]
    (map #(log-entry-from-change author time message rev %) changes)))

(defn commit-info [repo git-log]
  "returns the commit info of one git log entry"
  (qg/commit-info repo git-log))

(defn git-log [repo]
  "reads the git log from REPO"  
  (jg/git-log repo))

(defn to-log-entries [git-log]
  "transforms a GIT-LOG into a list of entries"
  (flatten (vector (map #(log-entries %) git-log))))

(defn read-git-log [path]
  "reads the git log of the repository located at PATH"
  (jg/with-repo path (map #(commit-info repo %) (git-log repo))))

(defn get-git-log [path]
  "reads the git log of the repository located at PATH"
  (-> (read-git-log path)
      (to-log-entries)))

(defn get-git-logs [paths]
  "reads the git log of each path"
  (reduce (fn [v path] (get-git-log path)) [] paths))

(defn get-git-log-async [path]
  "creates a go routine that writes the log into a channel and returns it"
  (let [git-log-channel (chan)]
    (go (>! git-log-channel (get-git-log path))
        (close! git-log-channel))
    git-log-channel))

(defn get-git-logs-async [paths]
  "creates a go routine for each path and collects the result into a vector"
  (reduce (fn [v path] (get-git-log-async path)) [] paths))
