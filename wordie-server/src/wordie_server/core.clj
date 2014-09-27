(ns wordie-server.core
  (:import java.io.ByteArrayInputStream)
  (:require [compojure.handler :as handler]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [wordie.merriam-webster :as mw]
            [cheshire.core :refer (generate-string)]
            [clojure.data.zip.xml :refer :all]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip :as zf]
  ))

(def base-url
  "http://www.dictionaryapi.com/api/v1/references/")

(def dictionary-url
  (str base-url "collegiate/xml/"))

(defn- zip-string
  [s]
  (zip/xml-zip (xml/parse (ByteArrayInputStream.
                            (.getBytes s "UTF-8")))))

(defn parse-xml
  [s]
  (let [xz (zip-string s)]
    (for [entry (xml-> xz :entry)]
      {:word (xml1-> entry :ew text)
       :spelling (xml1-> entry :hw text)
       :definitions (xml-> entry :def :dt text)})))

(defn query-dictionary
  [s]
  (let [url (str dictionary-url s "?key=" mw/dictionary-key)]
    (slurp url)))

(defn json-response
  [content]
  {:body (generate-string content)
   :headers {"Content-Type" "application/json; charset=utf-8"}})

(defroutes routes
  (GET "/api/dictionary" [query]
    (json-response (parse-xml (query-dictionary query)))))

(def handler
  (handler/api routes))

(comment

  (require '[ring.server.standalone :refer (serve)])
  (def handler (handler/api #'routes))
  (defonce server (serve #'handler {:auto-reload? false
                                    :open-browser? false}))

  )

