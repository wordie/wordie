(defproject wordie-server "0.1.0-SNAPSHOT"

  :description ""

  :url ""

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.logging "0.2.6"]
                 [org.slf4j/slf4j-api "1.6.1"]
                 [org.slf4j/slf4j-log4j12 "1.6.1"]
                 [ring/ring-core "1.2.2"]
                 [compojure "1.1.5"]
                 [cheshire "5.2.0"]
                 [org.clojure/data.zip "0.1.1"]
                 ]

  :plugins [[lein-ring "0.8.6"]]

  :profiles {:dev {:resource-paths ["profiles/dev"]
                   :dependencies [[ring-server "0.2.8"]]}}

  )

