(ns godotclj.bindings.types
  (:require [godotclj.clang :as clang]
            [godotclj.defs :as defs]))

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
