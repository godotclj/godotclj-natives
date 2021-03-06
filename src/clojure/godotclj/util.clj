(ns godotclj.util
  "Miscellaneous functions."
  (:require
   [godotclj.api :as api]
   [godotclj.bindings.godot :as godot]))

(defn class-map-merger
  "Merging function for `merge-with` than handles class maps."
  [old new]
  (if (and (map? old) (map? new))
    (merge-with class-map-merger old new)
    new))

(defn godot-project?
  [file]
  (and (.isDirectory file)
       (->> file
            .listFiles
            (map #(.getName %))
            (filter #{"project.godot"})
            seq)))

(defn simplify-method
  "Wrap Clojure function and make it usable for class-map."
  [f]
  (fn [instance _ _ n-args p-args]
    (apply f (api/->object instance)
           (seq (godot/->indexed-variant-array n-args p-args)))))

(defn map-vals
  "Map `f` to vals of `m`."
  [f m]
  (zipmap (keys m)
          (map f (vals m))))
