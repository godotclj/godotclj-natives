(ns godotclj.main
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn config-file
  []
  (or (when-let [path (some-> (System/getProperty "godotclj.config.path") io/file)]
        (when (.exists path)
          path))
      (io/file "godotclj.edn")))

(defn overrides-config-file
  "Override config keys -- used by godotclj to set the callbacks namespace"
  []
  (io/resource "godotclj.overrides.edn"))

(defn get-config
  []
  (merge {:callbacks {:namespace "godotclj.ffi.callback-stub"}}
         (some-> (config-file) slurp edn/read-string)
         (some-> (overrides-config-file) slurp edn/read-string)))

(defn get-main
  [config {:keys [runtime]}]
  (requiring-resolve (get-in config [:main runtime])))
