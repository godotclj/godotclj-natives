(ns godotclj.ffi.generator
  (:require [clojure.java.io :as io]
            [cognitect.transit :as transit]
            [godotclj.ffi.clang :as clang]
            [godotclj.ffi.defs :as defs])
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream]
           [java.util Locale]
           [org.scijava.nativelib NativeLibraryUtil]))

(defn arch
  []
  (.toLowerCase (.name (NativeLibraryUtil/getArchitecture)) java.util.Locale/ENGLISH))

(defn ->transit
  [data]
  (let [out (ByteArrayOutputStream.)
        w (transit/writer (io/output-stream out) :json)]
    (transit/write w data)
    (str out)))

(defn <-transit
  [^String s]
  (let [in     (ByteArrayInputStream. (.getBytes s))
        reader (transit/reader in :json)]
    (transit/read reader)))

(defn load-cache
  [cache]
  (<-transit (slurp (io/resource cache))))

(defn to-cache
  [cache result]
  (spit (doto (io/file cache)
          (io/make-parents))
        (->transit result)))

(defn generate-wrapper
  [dest]
  (clang/export-wrapper-fns defs/godot-function-bindings dest))

(defn cache-path
  [part]
  (.getPath (io/file (arch) part)))

(defn generate-enums
  [dest]
  (to-cache (io/file dest (:cache defs/enums))
            (clang/enums-map defs/enums)))

(defn generate-fns
  [dest]
  (to-cache (io/file dest (:cache defs/function-bindings))
            (apply merge
                   (for [fn-bind (:mappings defs/function-bindings)]
                     (clang/emit fn-bind)))))

(defn generate-structs
  [dest]
  (to-cache (io/file dest (:cache defs/structs))
            (reduce (fn [acc {:keys [names defs]}]
                      (-> acc
                          (update :names #(reduce conj % names))
                          (update :defs merge defs)))
                    {:names []
                     :defs  {}}
                    (map clang/generate-structs (:mappings defs/structs)))))

(defn generate-cache
  [cache-file]
  (let [cache-file (io/file cache-file)
        cache-name (.getName cache-file)
        dest       (.getParent cache-file)]
    (condp = cache-name
      (:cache defs/function-bindings)       (generate-fns dest)
      (:cache defs/structs)                 (generate-structs dest)
      (:cache defs/enums)                   (generate-enums dest))))
