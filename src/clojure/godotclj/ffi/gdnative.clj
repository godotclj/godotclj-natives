(ns godotclj.ffi.gdnative
  (:require [godotclj.ffi.clang :as clang]
            [godotclj.ffi.defs :as defs]
            ;; TODO clean up NS
            [tech.v3.datatype.struct :as dtype-struct]
            [tech.v3.datatype.protocols]
            [tech.v3.datatype.ffi :as dtype-ffi]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.native-buffer :as native-buffer]
            [tech.v3.datatype.struct :as dtype-struct])
  (:import [tech.v3.datatype.ffi Pointer]
           [clojure.lang Indexed Seqable]))

(def godot-struct-defs*
  (clang/define-structs defs/godot-structs))

(def wrapper-struct-defs*
  (clang/define-structs defs/wrapper-structs))

(def callback-struct-defs*
  (clang/define-structs defs/callback-structs))

(defn validate-defs!
  [defs]
  (doseq [[k v] defs]
    (assert (pos? (:datatype-size v)) (str k " has zero size!")))
  nil)

(validate-defs! godot-struct-defs*)
(validate-defs! wrapper-struct-defs*)
(validate-defs! callback-struct-defs*)

(def enums
  (clang/enums-map defs/enums))

(def fns
  (clang/emit defs/function-bindings))

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
