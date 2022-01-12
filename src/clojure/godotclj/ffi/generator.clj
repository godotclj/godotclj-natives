(ns godotclj.ffi.generator
  (:require [godotclj.ffi.clang :as clang]
            [godotclj.ffi.defs :as defs]))

(defn generate-wrapper
  [dest]
  (clang/export-wrapper-fns defs/godot-function-bindings dest))
