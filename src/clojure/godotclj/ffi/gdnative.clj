(ns godotclj.ffi.gdnative
  (:require [godotclj.ffi.clang :as clang]
            [godotclj.ffi.defs :as defs]
            [godotclj.ffi.generator :as generator]
            [tech.v3.datatype.ffi :as dtype-ffi]
            [clojure.java.io :as io]))

(def struct-defs*
  ;; TODO put in function or delay (since it reads from a file, this probably
  ;; shouldn't happen at compile time
  (clang/define-structs (generator/load-cache (generator/cache-path (:cache defs/structs)))))

(def enums
  (generator/load-cache (generator/cache-path (:cache defs/enums))))

(def fns
  (generator/load-cache (generator/cache-path (:cache defs/function-bindings))))

(defonce ^:private lib (dtype-ffi/library-singleton #'fns))

(defn set-library-instance!
  [lib-instance]
  (dtype-ffi/library-singleton-set-instance! lib lib-instance))

(defn- find-fn
  [fn-kwd]
  (dtype-ffi/library-singleton-find-fn lib fn-kwd))

(defmacro check-error
  [fn-def & body]
  body)

;; preventing "Method too large!"
(let [[fns-1 fns-2 fns-3] (partition-all 300 fns)]
  (def fns-1 fns-1)
  (def fns-2 fns-2)
  (def fns-3 fns-3))

(dtype-ffi/define-library-functions fns-1 find-fn check-error)
(dtype-ffi/define-library-functions fns-2 find-fn check-error)
(dtype-ffi/define-library-functions fns-3 find-fn check-error)
