(ns wordie-server.core
  (:require [compojure.handler :as handler]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [ring.util.response :as resp]
  ))


(defroutes routes
  (GET "/api" [word]
    word))

(def handler
  (handler/api routes))

(comment

  (require '[ring.server.standalone :refer (serve)])
  (def handler (handler/api #'routes))
  (defonce server (serve #'handler {:auto-reload? false
                                    :open-browser? false}))

  )

