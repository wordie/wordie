(ns wordie.core
  (:require [goog.dom :as dom]
            [om.core :as om :include-macros true]
            [wordie.components.sidebar :refer [sidebar-component]]
            [wordie.state :as a]))

(enable-console-print!)

(let [container (dom/createElement "div")
      body      (. js/document (querySelector "body"))]
  (set! (.-id container) "wordie-sidebar")
  (dom/appendChild body container)

  (om/root
   sidebar-component
   a/app-state
   {:target container}))
