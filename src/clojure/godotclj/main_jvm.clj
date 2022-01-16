(ns godotclj.main-jvm
  (:require [godotclj.ffi.gdnative :as gdnative]
            [godotclj.ffi.clang :as clang]
            [godotclj.ffi.defs :as defs]
            [godotclj.main :as main]
            [tech.v3.datatype.ffi :as dtype-ffi])
  (:import [tech.v3.datatype.ffi Pointer]))

;; The following is automatically configured by dtype-next,
;; based on "java --add-modules=jdk.incubator.foreign"
;; (cond (dtype-ffi/jdk-mmodel?)
;;       (require '[tech.v3.datatype.ffi.mmodel])
;;       (dtype-ffi/jna-ffi?)
;;       (require '[tech.v3.datatype.ffi.jna]))

(def libgodotclj-def
  (dtype-ffi/define-library
    gdnative/fns
    nil
    {:libraries ["godotclj_gdnative"]}))

(def libgodotclj-inst
  (dtype-ffi/instantiate-library libgodotclj-def "godotclj_gdnative"))

(defn godot_nativescript_init_clojure
  [p-h]
  (let [p-h (Pointer. p-h)]
    (gdnative/set-library-instance! libgodotclj-inst)

    (let [config (main/get-config)
          main   (main/get-main config {:runtime :jvm})]
      (gdnative/set_callback_namespace (dtype-ffi/string->c (name (get-in config [:callbacks :namespace]))))
      (main p-h))))
