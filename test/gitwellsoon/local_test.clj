(ns gitwellsoon.local-test
  (:require [clojure.test :refer :all]
            [gitwellsoon.local :refer :all]))

(deftest should-parse-time
  (is (= (#'gitwellsoon.local/parse-time (java.util.Date. 1478714862000)) "2016/11/09 19:07:42")))

(deftest should-create-log-entry-from-change
  (let [author "test-author"
        time  (java.util.Date. 1478714862000)
        message "test-message"
        rev "rev-0090290"
        changed-file ["/changed-file" :edit]]
    (is (= (#'gitwellsoon.local/log-entry-from-change author time message rev changed-file)
           {:author author
            :date (#'gitwellsoon.local/parse-time time)
            :entity "/changed-file"
            :loc-added 0,
            :loc-deleted 0,
            :message message
            :rev rev}))))

(deftest should-create-empty-log-entries
  (is (= (#'gitwellsoon.local/log-entries []) [])))

(deftest should-create-log-entries-for-changes []
  (is (= (#'gitwellsoon.local/log-entries {:author "author" 
                         :date (java.util.Date. 1478714862000) 
                         :message "message"
                         :id "rev-" 
                         :changed_files [["/changed-file1" :add], ["changed-file2" :edit]]})
         [{:author "author", 
           :date "2016/11/09 19:07:42", 
           :entity "/changed-file1", 
           :loc-added 0, 
           :loc-deleted 0, 
           :message "message", 
           :rev "rev-"},
          {:author "author", 
           :date "2016/11/09 19:07:42",
           :entity "changed-file2", 
           :loc-added 0, 
           :loc-deleted 0, 
           :message "message", 
           :rev "rev-"}])))
  
(deftest should-create-log-entries-for-changes-fail
  (not (= (#'gitwellsoon.local/log-entries {:author "author" 
                          :date (java.util.Date. 1478714862000) 
                          :message "message"
                          :id "rev-" 
                          :changed_files [["/changed-file1" :add], ["changed-file2" :edit]]}) [] )))

(deftest should-get-git-log
  (with-redefs-fn {#'gitwellsoon.local/read-git-log (fn [project] [{:author "author" 
                                                                    :date (java.util.Date. 1478714862000) 
                                                                    :message "message"
                                                                    :id "rev-" 
                                                                    :changed_files [["/changed-file1" :add], ["changed-file2" :edit]]}])}
    #(is (= {:name "test" :log [{:author "author", 
                                 :date "2016/11/09 19:07:42", 
                                 :entity "/changed-file1", 
                                 :loc-added 0, 
                                 :loc-deleted 0, 
                                 :message "message", 
                                 :rev "rev-"},
                                {:author "author", 
                                 :date "2016/11/09 19:07:42", 
                                 :entity "changed-file2", 
                                 :loc-added 0, 
                                 :loc-deleted 0, 
                                 :message "message", 
                                 :rev "rev-"}]} (get-git-log {:name "test" :path "/tmp"})))))

(deftest should-get-git-logs
  (with-redefs-fn {#'gitwellsoon.local/read-git-log (fn [project] [{:author "author" 
                                                                    :date (java.util.Date. 1478714862000) 
                                                                    :message "message"
                                                                    :id "rev-" 
                                                                    :changed_files [["/changed-file1" :add], ["changed-file2" :edit]]}])}
    #(is (= {:name "test" :log [
                                {:author "author", 
                                 :date "2016/11/09 19:07:42", 
                                 :entity "/changed-file1", 
                                 :loc-added 0, 
                                 :loc-deleted 0, 
                                 :message "message", 
                                 :rev "rev-"},
                                {:author "author", 
                                 :date "2016/11/09 19:07:42", 
                                 :entity "changed-file2", 
                                 :loc-added 0, 
                                 :loc-deleted 0, 
                                 :message "message", 
                                 :rev "rev-"}]} 
            (get-git-log {:name "test" :path "/tmp"})))))

(deftest should-get-git-logs
  (with-redefs-fn {#'gitwellsoon.local/read-git-log (fn [project] [{:author "author" 
                                                                    :date (java.util.Date. 1478714862000) 
                                                                    :message "message"
                                                                    :id "rev-" 
                                                                    :changed_files [["/changed-file1" :add], ["changed-file2" :edit]]}])}
    #(is (= [{:name "test" :log [
                                 {:author "author", 
                                  :date "2016/11/09 19:07:42", 
                                  :entity "/changed-file1", 
                                  :loc-added 0, 
                                  :loc-deleted 0, 
                                  :message "message", 
                                  :rev "rev-"},
                                 {:author "author", 
                                  :date "2016/11/09 19:07:42", 
                                  :entity "changed-file2", 
                                  :loc-added 0, 
                                  :loc-deleted 0, 
                                  :message "message", 
                                  :rev "rev-"}]}] 
            (get-git-logs [{:name "test" :path "/tmp"}])))))

(deftest should-get-git-logs-async
  (with-redefs-fn {#'gitwellsoon.local/read-git-log (fn [project] [{:author "author" 
                                                  :date (java.util.Date. 1478714862000) 
                                                  :message "message"
                                                  :id "rev-" 
                                                  :changed_files [["/changed-file1" :add], ["changed-file2" :edit]]}])}
    #(is (= [{:name "test" :log [
                                  {:author "author", 
                                   :date "2016/11/09 19:07:42", 
                                   :entity "/changed-file1", 
                                   :loc-added 0, 
                                   :loc-deleted 0, 
                                   :message "message", 
                                   :rev "rev-"},
                                  {:author "author", 
                                   :date "2016/11/09 19:07:42", 
                                   :entity "changed-file2", 
                                   :loc-added 0, 
                                   :loc-deleted 0, 
                                   :message "message", 
                                   :rev "rev-"}]}] 
            (get-git-logs-async [{:name "test" :path "/tmp"}])))))
