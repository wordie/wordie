(defproject wordie "0.1.0-SNAPSHOT"
  :description "Wordie shows you everything you need to know about words you encounter right inside your browser."
  :url "http://wordie.clojurecup.com"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2356"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [om "0.7.3"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]]

  :source-paths ["src"]

  :cljsbuild {:builds {:dev
                       {:source-paths ["src"]
                        :compiler     {:output-to        "resources/wordie.js"
                                       :output-dir       "target"
                                       :optimizations    :none
                                       :source-map       true
                                       :preamble         ["../resources/vendor/react-0.11.1/react-with-addons.min.js"]
                                       :externs          ["../resources/vendor/react-0.11.1/react-with-addons.js"]
                                       :closure-warnings {:externs-validation :off
                                                          :non-standard-jsdoc :off}}}
                       :prod
                       {:source-paths ["src"]
                        :compiler     {:output-to        "resources/wordie.min.js"
                                       :output-dir       "out"
                                       :optimizations    :advanced
                                       :pretty-print     false
                                       :preamble         ["../resources/vendor/react-0.11.1/react-with-addons.min.js"]
                                       :externs          ["../resources/vendor/react-0.11.1/react-with-addons.js", "../externs.js"]
                                       :closure-warnings {:externs-validation :off
                                                          :non-standard-jsdoc :off}}}}})
