(ns wordie-server.core
  (:require [compojure.handler :as handler]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [wordie-server.merriam-webster.api :as merriam-webster-api]
            [wordie-server.yandex.api :as yandex-api]
            [cheshire.core :refer (generate-string)]
            ))

(defn json-response
  [content]
  {:body (generate-string content)
   :headers {"Content-Type" "application/json; charset=utf-8"}})

(defroutes routes

  (GET "/api/dictionary" [query]
    (json-response (merriam-webster-api/query-dictionary query)))

  (GET "/api/detect" [query]
    (json-response (yandex-api/detect-language query)))

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

(comment

  (require '[ring.server.standalone :refer (serve)])

  (def handler
    (-> #'routes
        handler/api
        wrap-cors))

  (defonce server (serve #'handler {:auto-reload? false
                                    :open-browser? false}))

  )

