(ns godotclj.ffi.callback)

(defn instance_method_callback
  [arg-ptr]
  (throw (ex-info "gdnative/set_callback_namespace must be called with the location of instance_method_callback. Default callback in godotclj.ffi.callback has been called, but must be overridden." {})))
