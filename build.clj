(ns build
  (:require [clojure.tools.build.api :as b]))

(def lib 'io.github.godotclj/godotclj-natives)

;;(def version (format "0.0.%s" (b/git-count-revs nil)))
(def version "0.0.6-SNAPSHOT")
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
#_ (def jar-file (format "target/%s-%s.jar" (name lib) version))
(def jar-file (format "target/%s.jar" (name lib)))

(defn clean [_]
  (b/delete {:path "target"}))

(defn jar [_]
  (b/write-pom {:class-dir class-dir
                :lib       lib
                :version   version
                :basis     basis
                :src-dirs  ["src/clojure"]})
  (b/copy-dir {:src-dirs   ["src/clojure" "classes" "build/lib" "build/cache" "build/godot-headers"]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file jar-file}))

(defn prep
  "Prepare library for use from another deps project"
  [_]
  (b/process {:command-args ["git" "submodule" "update" "--init" "--recursive"]})
  (b/process {:command-args ["make" "ARCH=linux_64"]}))
