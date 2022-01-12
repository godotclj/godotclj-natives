(ns godotclj.natives-test.main-jvm
  (:require [nrepl.cmdline]
            [godotclj.ffi.gdnative :as gdnative]
            [tech.v3.datatype.ffi :as dtype-ffi]
            [tech.v3.datatype.struct :as dtype-struct]))

(defonce repl
  (delay
    (try
      (apply nrepl.cmdline/-main
             ["--middleware"
              "[\"refactor-nrepl.middleware/wrap-refactor\", \"cider.nrepl/cider-middleware\"]"])

      (catch Exception e
        (println e)))))

(defn alloc
  [type-name]
  (dtype-struct/new-struct type-name {:container-type :native-heap}))

(defn register-methods
  [p-h]
  (future @repl)
  (let [create  (alloc :godot-instance-create-func)
        destroy (alloc :godot-instance-destroy-func)]

    (gdnative/get_godot_instance_create_func (dtype-ffi/->pointer create))
    (gdnative/get_godot_instance_destroy_func (dtype-ffi/->pointer destroy))

    (gdnative/godot_nativescript_register_class_wrapper p-h
                                                        (dtype-ffi/string->c "Main")
                                                        (dtype-ffi/string->c "Node")
                                                        (dtype-ffi/->pointer create)
                                                        (dtype-ffi/->pointer destroy))

    (let [method      (alloc :godot-instance-method)
          attributes  (alloc :godot-method-attributes)]

      (gdnative/get_godot_instance_method (dtype-ffi/->pointer method))
      (.put attributes :rpc-type 0)
      (gdnative/godot_nativescript_register_method_wrapper p-h
                                                           (dtype-ffi/string->c "Main")
                                                           (dtype-ffi/string->c "_ready")
                                                           (dtype-ffi/->pointer attributes)
                                                           (dtype-ffi/->pointer method)))))
