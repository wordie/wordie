(defproject wordie "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://wordie.clojurecup.com"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2356"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]]

  :source-paths ["src"]

  :cljsbuild {:builds {:dev
                       {:source-paths ["src"]
                        :compiler {:output-to     "package/wordie.js"
                                   :output-dir    "package"
                                   :optimizations :none
                                   :source-map    true}}
                       :prod
                       {:source-paths ["src"]
                        :compiler {:output-to     "package/wordie.min.js"
                                   :optimizations :advanced
                                   :pretty-print  false}}}})
