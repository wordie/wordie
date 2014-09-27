(ns wordie.components.sidebar
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defn sidebar-component
  [node owner]
  (reify
    om/IRenderState
    (render-state [_ {:keys [state]}]
      (dom/div #js {:className "wordie-sidebar open"}
               (dom/div #js {:className "wordie-toggle"}
                        "")
               (dom/div #js {:className "wordie-content"}
                        (dom/div #js {:className "wordie-header"}
                                 "state"))))))

