(ns godotclj.ffi.test-runner
  (:require [kaocha.repl :as koacha]))

(defn instance_method_callback
  "Called from godot Main._ready -- set in godotclj-test.edn, via godotclj.main-jvm"
  [_]
  (let [result (koacha/run :main)]
    ;; {:kaocha.result/count 2, :kaocha.result/pass 1, :kaocha.result/error 0, :kaocha.result/fail 1, :kaocha.result/pending 0}
    (if (= (:kaocha.result/count result)
           (:kaocha.result/pass result))
      (System/exit 0)
      (System/exit 1))))
