(ns wordie.components.sidebar
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [clojure.string :as clj-str]
            [cljs.core.async :as async :refer [chan put!]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [wordie.events :refer [selection server-channel]]))

;;
;; Actions
;;

(defn toggle-sidebar
  [state]
  (om/transact! state [:sidebar :open] not))

(defn load-definition
  [state phrase r]
  (om/update! state [:sidebar :open] true)
  (let [status (get-in @state [:main :status])]
    (when-not (= status :loading)
      (om/update! state [:main :status] :loading)
      (put! r [:dictionary-query (str "http://wordie.clojurecup.com/api/dictionary?query=" phrase)]))))

(defn strip-numbers
  [s]
  (.trim (clj-str/replace s "[0-9]" "")))

(defn lookup-language
  [state phrase r]
  (put! r [:detect-language (str "http://wordie.clojurecup.com/api/detect?query=" (strip-numbers phrase))]))

(defn show-definition
  [state data]
  (om/transact! state [:main] #(assoc % :status :loaded :data data)))

(defn switch-to-error
  [state]
  (om/update! state [:main :status] :failed))

;;
;; Events
;;

(defn on-toggle-click
  [_ commands]
  (put! commands [:toggle nil]))

;;
;; Components
;;

(defn definition-view
  [data owner]
  (reify
    om/IRender
    (render [_]
      (let [{:keys [word spelling definitions]} data]
        (dom/div #js {:className "wordie-definition"}
                 (dom/div #js {:className "wordie-word"}
                           word)
                 (dom/div #js {:className "wordie-spelling"}
                           spelling)
                 (apply dom/ul #js {:className "wordie-definition-entries"}
                        (for [definition definitions]
                          (dom/li #js {:className "wordie-definition-entry"}
                                  definition))))))))

(defn sidebar-content-component
  [state owner]
  (reify
    om/IRender
    (render [_]
      (let [{:keys [status data language]} state]
        (dom/div #js {:className "wordie-content"}
                 (case status
                   :loading
                   (dom/div #js {:className "wordie-spinner"}"")
                   :loaded
                   (if (seq data)
                     (apply dom/div #js {:className "wordie-definitions-list"}
                            (om/build-all definition-view data))
                     (dom/div #js {:className "wordie-message"}
                              (str "Looks like we don't know that word."
                                   ; FIXME: Hacky inline check
                                   (if (= language "en")
                                     ""
                                     " We currently support only English."))))
                   :failed
                   (dom/div #js {:className "wordie-message error"}
                            "We are sorry, but we could not contact our servers. Please try again later.")
                   (dom/div #js {:className "wordie-message"}
                            "Select a word or a phrase on the page to see its definition.")))))))

(defn handle-loading-success
  [state [event-type data]]
  (case event-type
    :dictionary-query (show-definition state data)
    :detect-language  (om/transact! state [:main] #(assoc % :language data))))

(defn handle-loading-error
  [state [event-type _]]
  (case event-type
    :dictionary-query (switch-to-error state)
    :detect-language nil)) ; TODO: What is the expected behavior in this case?

(defn sidebar-component
  [state owner]
  (reify
    om/IInitState
    (init-state [_]
      (let [{:keys [in out]} (server-channel)]
        {:commands (async/merge [(selection) out])
         :requests in}))

    om/IWillMount
    (will-mount [_]
      (let [commands (om/get-state owner :commands)
            requests (om/get-state owner :requests)]
        (lookup-language state document.title requests)
        (go-loop []
          (when-let [[command data] (<! commands)]
            (case command
              :toggle           (toggle-sidebar state)
              :select           (load-definition state (:text data) requests)
              :loading-success  (handle-loading-success state data)
              :loading-error    (handle-loading-error state data)
              nil)
            (recur)))))

    om/IRenderState
    (render-state [_ {:keys [commands]}]
      (let [open   (get-in state [:sidebar :open] false)]
        (dom/div #js {:className (str "wordie-sidebar" (if open " open" " closed"))}
                 (dom/div #js {:className "wordie-toggle"
                               :onClick   #(on-toggle-click % commands)} "")
                 (om/build sidebar-content-component (:main state)))))))

