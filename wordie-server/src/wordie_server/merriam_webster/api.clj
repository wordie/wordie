(ns wordie-server.merriam-webster.api
  (:import java.net.URLEncoder
           java.io.ByteArrayInputStream)
  (:require [wordie-server.merriam-webster.keys :as mw-keys]
            [clojure.data.zip.xml :refer :all]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip :as zf]))

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
  (let [url (str dictionary-url (URLEncoder/encode s) "?key=" mw-keys/dictionary)]
    (parse-xml (slurp url))))

