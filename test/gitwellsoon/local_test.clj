(ns gitwellsoon.local-test
  (:require [clojure.test :refer :all]
            [gitwellsoon.local :refer :all]))

(deftest should-parse-time
(is (= (#'parse-time (java.util.Date. 1478714862000)) "2016/11/09 19:07:42")))

(deftest should-create-log-entry-from-change
(let [author "test-author"
      time  (java.util.Date. 1478714862000)
      message "test-message"
      rev "rev-0090290"
      changed-file ["/changed-file" :edit]]
  (is (= (#'log-entry-from-change author time message rev changed-file)
         {:author author
          :time (#'parse-time time)
          :entity "/changed-file"
          :loc-added 0,
          :loc-deleted 0,
          :message message
          :rev rev}))))

(deftest should-create-empty-log-entries
  (is (= (#'log-entries []) [])))

(deftest should-create-log-entries-for-changes []
  (is (= (#'log-entries {:author "author" 
                         :time (java.util.Date. 1478714862000) 
                         :message "message"
                         :id "rev-" 
                         :changed_files [["/changed-file1" :add], ["changed-file2" :edit]]})
         [{:author "author", 
           :time "2016/11/09 19:07:42", 
           :entity "/changed-file1", 
           :loc-added 0, 
           :loc-deleted 0, 
           :message "message", 
           :rev "rev-"},
          {:author "author", 
           :time "2016/11/09 19:07:42", 
           :entity "changed-file2", 
           :loc-added 0, 
           :loc-deleted 0, 
           :message "message", 
           :rev "rev-"}])))
  
(deftest should-create-log-entries-for-changes-fail
  (not (= (#'log-entries {:author "author" 
                          :time (java.util.Date. 1478714862000) 
                          :message "message"
                          :id "rev-" 
                          :changed_files [["/changed-file1" :add], ["changed-file2" :edit]]}) [] )))

(deftest should-get-git-log
  (with-redefs-fn {#'read-git-log (fn [project] [{:author "author" 
                                                  :time (java.util.Date. 1478714862000) 
                                                  :message "message"
                                                  :id "rev-" 
                                                  :changed_files [["/changed-file1" :add], ["changed-file2" :edit]]}])}
    #(is (= {:name "test" :log [{:author "author", 
                                 :time "2016/11/09 19:07:42", 
                                 :entity "/changed-file1", 
                                 :loc-added 0, 
                                 :loc-deleted 0, 
                                 :message "message", 
                                 :rev "rev-"},
                                {:author "author", 
                                 :time "2016/11/09 19:07:42", 
                                 :entity "changed-file2", 
                                 :loc-added 0, 
                                 :loc-deleted 0, 
                                 :message "message", 
                                 :rev "rev-"}]} (get-git-log {:name "test" :path "/tmp"})))))

(deftest should-get-git-logs
  (with-redefs-fn {#'read-git-log (fn [project] [{:author "author" 
                                                  :time (java.util.Date. 1478714862000) 
                                                  :message "message"
                                                  :id "rev-" 
                                                  :changed_files [["/changed-file1" :add], ["changed-file2" :edit]]}])}
    #(is (= {:name "test" :log [
                                {:author "author", 
                                 :time "2016/11/09 19:07:42", 
                                 :entity "/changed-file1", 
                                 :loc-added 0, 
                                 :loc-deleted 0, 
                                 :message "message", 
                                 :rev "rev-"},
                                {:author "author", 
                                 :time "2016/11/09 19:07:42", 
                                 :entity "changed-file2", 
                                 :loc-added 0, 
                                 :loc-deleted 0, 
                                 :message "message", 
                                 :rev "rev-"}]} 
            (get-git-log {:name "test" :path "/tmp"})))))
