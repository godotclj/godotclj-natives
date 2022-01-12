(ns godotclj.ffi.gdnative-test
  (:require [clojure.test :refer [deftest is]]
            [godotclj.ffi.gdnative :as gdnative]
            [godotclj.ffi.test-utils :as utils]
            [tech.v3.datatype.ffi :as dtype-ffi]))

(deftest get-instance-by-id-test
  (let [ob-type "Node"
        p-ob    (utils/construct "Node")
        f       (gdnative/godot_method_bind_get_method_wrapper (dtype-ffi/string->c ob-type)
                                                               (dtype-ffi/string->c "get_instance_id"))
        result  (utils/alloc :godot-variant)]
    (gdnative/godot_method_bind_call_wrapper f p-ob nil 0 nil (dtype-ffi/->pointer result))
    (is (= (.address (-> (gdnative/godot_variant_as_int_wrapper result)
                         (gdnative/godot_instance_from_id_wrapper)))
           (.address p-ob)))))
