(ns wordie-server.wikipedia.api
  (:import java.net.URLEncoder)
  (:require [cheshire.core :refer (parse-string)]))

(def url
  "http://en.wikipedia.org/w/api.php?action=query&prop=extracts&format=json&redirects&exchars=2048")

(defn intro
  [s]
  (let [url (str url "&titles=" (URLEncoder/encode s))]
    (-> (parse-string (slurp url))
        (get-in ["query" "pages"])
        vals)))
