(ns wordie.components.sidebar
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :as async :refer [chan put!]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defn toggle-sidebar
  [state]
  (om/transact! state [:sidebar :open] not))

(defn on-toggle-click
  [_ commands]
  (put! commands [:toggle nil]))

;;
;;
;;

(defn sidebar-component
  [state owner]
  (reify
    om/IInitState
    (init-state [_]
      {:commands (chan)})

    om/IWillMount
    (will-mount [_]
      (let [commands (om/get-state owner :commands)]
        (go-loop []
                 (when-let [[command data] (<! commands)]
                   (case command
                     :toggle (toggle-sidebar state)
                     nil)
                   (recur)))))

    om/IRenderState
    (render-state [_ {:keys [commands]}]
      (let [open (get-in state [:sidebar :open] false)]
        (dom/div #js {:className (str "wordie-sidebar" (if open " open" " closed"))}
                 (dom/div #js {:className "wordie-toggle"
                               :onClick   #(on-toggle-click % commands)}
                          "")
                 (dom/div #js {:className "wordie-content"}
                          (dom/div #js {:className "wordie-header"}
                                   "state")))))))

