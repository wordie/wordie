(ns wordie-server.yandex.api
  (:import java.net.URLEncoder)
  (:require [wordie-server.yandex.keys :as yx-keys]
            [cheshire.core :refer (parse-string)]))

(def detect-url
  "https://translate.yandex.net/api/v1.5/tr.json/detect")

(defn detect-language
  [s]
  (let [url (str detect-url "?key=" yx-keys/api "&text=" (URLEncoder/encode s))]
    (get (parse-string (slurp url)) "lang")))

