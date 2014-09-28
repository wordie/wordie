(ns wordie-server.merriam-webster.api
  (:import java.net.URLEncoder
           java.io.ByteArrayInputStream)
  (:require [wordie-server.merriam-webster.keys :as mw-keys]
            [clojure.data.zip.xml :refer :all]
            [clojure.xml :as xml]
            [clojure.zip :as zip]
            [clojure.data.zip :as zf]
            [clojure.string :as clj-str]))

(def base-url
  "http://www.dictionaryapi.com/api/v1/references/")

(def dictionary-url
  (str base-url "collegiate/xml/"))

(def thesaurus-url
  (str base-url "thesaurus/xml/"))

(defn- zip-string
  [s]
  (zip/xml-zip (xml/parse (ByteArrayInputStream.
                            (.getBytes s "UTF-8")))))

(defn as-xml
  [node]
  (clj-str/replace (with-out-str (xml/emit-element node)) "\n" ""))

(defn parse-dictionary-xml
  [s]
  (let [xz (zip-string s)]
    (for [entry (xml-> xz :entry)]
      {:word (xml1-> entry :ew text)
       :spelling (xml1-> entry :hw text)
       :definitions (xml-> entry :def :dt zip/node as-xml)})))

(defn build-query-url
  [url k s]
  (str url (URLEncoder/encode (.toLowerCase s)) "?key=" k))

(def build-dictionary-query-url
  (partial build-query-url dictionary-url mw-keys/dictionary))

(defn query-dictionary
  [s]
  (parse-dictionary-xml (slurp (build-dictionary-query-url s))))

(def build-thesaurus-query-url
  (partial build-query-url thesaurus-url mw-keys/thesaurus))

(defn parse-thesaurus-xml
  [s]
  (let [xz (zip-string s)]
    (for [entry (xml-> xz :entry)]
      {:word (xml1-> entry :term :ew text)
       :spelling (xml1-> entry :term :hw text)
       :definitions (xml-> entry :sens zip/node as-xml)})))

(defn query-thesaurus
  [s]
  (parse-thesaurus-xml (slurp (build-thesaurus-query-url s))))

