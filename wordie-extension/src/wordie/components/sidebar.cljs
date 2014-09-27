(ns wordie.components.sidebar
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [cljs.core.async :as async :refer [chan put!]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [wordie.events :refer [selection]]))

;;
;; Actions
;;

(defn toggle-sidebar
  [state]
  (om/transact! state [:sidebar :open] not))

(defn show-definition
  [state phrase]
  (om/update! state [:main] {:status :loaded
                             :phrase phrase}))

;;
;; Events
;;

(defn on-toggle-click
  [_ commands]
  (put! commands [:toggle nil]))

;;
;; Components
;;

(defn sidebar-content-component
  [state owner]
  (reify
    om/IRender
    (render [_]
      (let [{:keys [status phrase]} state]
        (dom/div #js {:className "wordie-content"}
                 (case status
                   :loading
                   (dom/div #js {:className "wordie-spinner"}"")
                   :loaded
                   (dom/div #js {:className "wordie-header"}
                            phrase)
                   (dom/div #js {:className "wordie-message"}"Select a word or a phrase on the page to see its definition.")))))))

(defn sidebar-component
  [state owner]
  (reify
    om/IInitState
    (init-state [_]
      {:commands (selection)})

    om/IWillMount
    (will-mount [_]
      (let [commands (om/get-state owner :commands)]
        (go-loop []
                 (when-let [[command data] (<! commands)]
                   (case command
                     :toggle (toggle-sidebar state)
                     :select (show-definition state (:text data))
                     nil)
                   (recur)))))

    om/IRenderState
    (render-state [_ {:keys [commands]}]
      (let [open   (get-in state [:sidebar :open] false)
            phrase (get-in state [:main :phrase] "-")]
        (dom/div #js {:className (str "wordie-sidebar" (if open " open" " closed"))}
                 (dom/div #js {:className "wordie-toggle"
                               :onClick   #(on-toggle-click % commands)} "")
                 (om/build sidebar-content-component (:main state)))))))

