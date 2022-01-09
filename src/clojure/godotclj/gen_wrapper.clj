(ns godotclj.gen-wrapper
  (:require [godotclj.clang :as clang]
            [godotclj.defs :as defs]))

(defn generate
  [dest]
  (clang/export-wrapper-fns defs/godot-function-bindings dest))
