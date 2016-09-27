(ns gitwellsoon.code-maat
  (:require [incanter.core                       :as incanter]
            [code-maat.analysis.logical-coupling :as coupling]
            [code-maat.analysis.authors          :as authors]))

(def default-opts {:min-revs 5
                   :min-shared-revs 5
                   :min-coupling 30
                   :max-coupling 100
                   :max-changeset-size 30})

(defn to-maps [data]
  (let [col-names (incanter/col-names data)]
    (->> data
         incanter/to-list
         (map #(zipmap col-names %)))))

(def analysis-map
  {:coupling coupling/by-degree
   :authors authors/by-count})

(defn run-analysis [analysis-key data]
  (when-let [analysis (analysis-map analysis-key)]
    (-> data
        incanter/to-dataset
        (analysis default-opts)
        to-maps)))
