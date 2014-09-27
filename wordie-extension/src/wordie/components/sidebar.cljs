(ns wordie.components.sidebar
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defn sidebar-component
  [node owner]
  (reify
    om/IRenderState
    (render-state [_ {:keys [state]}]
      (dom/div #js {:className "df-sidebar closed"}
               (dom/div #js {:className "df-toggle"}
                        "")
               (dom/div #js {:className "df-content"}
                        (dom/div #js {:className "df-title"}
                                 "state"))))))

