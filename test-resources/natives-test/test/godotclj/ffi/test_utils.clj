(ns godotclj.ffi.test-utils
  (:require [godotclj.ffi.gdnative :as gdnative]
            [tech.v3.datatype.ffi :as dtype-ffi]
            [tech.v3.datatype.struct :as dtype-struct])
  (:import com.sun.jna.Function))

(defn alloc
  [type-name]
  (dtype-struct/new-struct type-name {:container-type :native-heap}))

(defn call-pointer-function
  ([ptr]
   (call-pointer-function ptr nil))
  ([ptr args]
   (-> (.invoke (Function/getFunction (com.sun.jna.Pointer. (.address ptr)))
                com.sun.jna.Pointer
                (to-array args))
       com.sun.jna.Pointer/nativeValue
       (tech.v3.datatype.ffi.Pointer.))))

(defn construct
  [class-name]
  (let [constructor-wrapper (alloc :godot-class-constructor-wrapper)]
    (gdnative/godot_get_class_constructor_wrapper (dtype-ffi/string->c class-name)
                                                  (dtype-ffi/->pointer constructor-wrapper))

    (call-pointer-function (tech.v3.datatype.ffi.Pointer. (:value constructor-wrapper)))))
