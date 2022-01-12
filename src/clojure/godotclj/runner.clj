(ns godotclj.runner
  (:require [babashka.process :refer [process check]]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [godotclj.natives :as natives]))

(defn absolute-class-path
  [cp]
  (str/join ":" (map (fn [path]
                       (.getAbsolutePath (io/file path)))
                     (str/split cp #":"))))

(defn java-home
  []
  (System/getProperty "java.home"))

(defn class-path
  []
  (absolute-class-path (System/getProperty "java.class.path")))

(defn start
  [& args]
  (let [JAVA_HOME         (java-home)
        LD_LIBRARY_PATH   (System/getenv "LD_LIBRARY_PATH")
        JAVA_TOOL_OPTIONS (or (System/getenv "JAVA_TOOL_OPTIONS") "")]
    (natives/extract-native-libraries)

    (let [result (process `["godot" ~@args]
                          {:err :inherit
                           :out :inherit
                           :env (merge (into {} (System/getenv))
                                       {"JAVA_HOME"         JAVA_HOME
                                        "JAVA_TOOL_OPTIONS" JAVA_TOOL_OPTIONS
                                        "CLASSPATH"         (class-path)
                                        "LD_LIBRARY_PATH"
                                        (format "%s/lib:%s/lib/server:%s:%s"
                                                JAVA_HOME
                                                JAVA_HOME
                                                (.getAbsolutePath (io/file "natives"))
                                                (or LD_LIBRARY_PATH ""))})})]
      (try
        (check result)
        (catch Exception e
          (shutdown-agents)
          (System/exit 1)))

      (shutdown-agents)
      (System/exit 0))))
