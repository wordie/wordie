(ns wordie-server.core
  (:require [compojure.handler :as handler]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [wordie-server.merriam-webster.api :as merriam-webster-api]
            [wordie-server.yandex.api :as yandex-api]
            [wordie-server.wikipedia.api :as wikipedia-api]
            [cheshire.core :refer (generate-string)]
            [ring.server.standalone :refer (serve)]
            )
  (:gen-class))

(defn response
  [content]
  {:body (pr-str content)
   :headers {"Content-Type" "application/edn; charset=utf-8"}})

(defroutes routes

  (GET "/api/dictionary" [query]
    (response (merriam-webster-api/query-dictionary query)))

  (GET "/api/thesaurus" [query]
    (response (merriam-webster-api/query-thesaurus query)))

  (GET "/api/detect" [query]
    (response (yandex-api/detect-language query)))

  (GET "/api/encyclopedia" [query]
    (response (wikipedia-api/intro query)))

  )

(defn wrap-cors
  [handler]
  (fn [req]
    (when-let [res (handler req)]
      (resp/header res "Access-Control-Allow-Origin" "*"))))

(def handler
  (-> routes
      handler/api
      wrap-cors))

(def jetty-config
  {:auto-reload? false
   :open-browser? false})

(defn -main
  [& args]
  (serve handler jetty-config))

(comment

  (def handler
    (-> #'routes
        handler/api
        wrap-cors))

  (defonce server (serve #'handler
                         (assoc jetty-config :port 3001)))

  )

