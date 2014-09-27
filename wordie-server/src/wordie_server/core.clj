(ns wordie-server.core
  (:require [compojure.handler :as handler]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [wordie.merriam-webster :as mw]
            [cheshire.core :refer (generate-string)]
  ))

(def base-url
  "http://www.dictionaryapi.com/api/v1/references/")

(def dictionary-url
  (str base-url "collegiate/xml/"))

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
    (json-response (query-dictionary query))))

(def handler
  (handler/api routes))

(comment

  (require '[ring.server.standalone :refer (serve)])
  (def handler (handler/api #'routes))
  (defonce server (serve #'handler {:auto-reload? false
                                    :open-browser? false}))

  )

