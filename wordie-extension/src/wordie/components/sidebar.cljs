(ns wordie.components.sidebar
  (:require-macros [cljs.core.async.macros :refer [go-loop]])
  (:require [clojure.string :as clj-str]
            [cljs.core.async :as async :refer [chan put!]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [wordie.events :refer [messages selection server-channel storage-channel]]))

;;
;; Actions
;;

(defn toggle-sidebar
  [state]
  (om/transact! state [:sidebar :open] not))

(defn load-definition
  [state phrase r]
  (let [snapshot @state]
    (when (get-in snapshot [:sidebar :enabled])
      (om/update! state [:sidebar :open] true)
      (let [status (get-in snapshot [:main :status])]
        (when-not (= status :loading)
          (om/update! state [:main :status] :loading)
          (put! r [:dictionary-query (str "http://wordie.clojurecup.com/api/dictionary?query=" phrase)]))))))

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

(defn switch-wordie-status
  [state status]
  (om/update! state [:sidebar :enabled] status))

(defn activate-tab
  [state tab]
  (when-not (= tab (get-in @state [:main :tab]))
    (om/update! state [:main :tab] tab)))

;;
;; Events
;;

(defn on-toggle-click
  [_ commands]
  (put! commands [:toggle nil]))

(defn on-tab-selection
  [_ commands tab]
  (put! commands [:change-tab tab]))

;;
;; Handlers
;;

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

(defn handle-storage-read-response
  [state [event-type data]]
  (case event-type
    :wordie-status (switch-wordie-status state (get data "wordieEnabled" false))
    nil))

(defn handle-internal-message
  [state data]
  (let [status (get data "wordieEnabled")]
    (when-not (nil? status)
      (switch-wordie-status state status))))

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
                          (dom/li #js {:className "wordie-definition-entry"
                                       :dangerouslySetInnerHTML #js {:__html definition}}
                                  nil))))))))

(defn sidebar-content-component
  [state owner]
  (reify
    om/IRenderState
    (render-state [_ {:keys [commands]}]
      (let [{:keys [status data language tab]} state]
        (dom/div #js {:className "wordie-content"}
                 (dom/div #js {:className "wordie-tab-panel"}
                          (dom/div #js {:className (str "wordie-tab"
                                                        (when (= tab :definition)
                                                          " active"))
                                        :onClick  #(on-tab-selection % commands :definition)}
                                   "Definition")
                          (dom/div #js {:className (str "wordie-tab"
                                                        (when (= tab :thesaurus)
                                                          " active"))
                                        :onClick  #(on-tab-selection % commands :thesaurus)}
                                   "Thesaurus"))
                 (case status
                   :loading
                   (dom/div #js {:className "wordie-spinner"}"")
                   :loaded
                   (if (seq data)
                     (apply dom/div #js {:className "wordie-definitions-list"}
                            (om/build-all definition-view data))
                     (dom/div #js {:className "wordie-message"}
                              (str "Looks like we don't know that word."
                                   (when-not (= language "en")
                                     " We currently support only English."))))
                   :failed
                   (dom/div #js {:className "wordie-message error"}
                            "We are sorry, but we could not contact our servers. Please try again later.")
                   (dom/div #js {:className "wordie-message"}
                            "Select a word or a phrase on the page to see its definition.")))))))

(defn sidebar-component
  [state owner]
  (reify
    om/IInitState
    (init-state [_]
      (let [{server-in :in server-out :out}   (server-channel)
            {storage-in :in storage-out :out} (storage-channel)]
        {:commands (async/merge [(messages) (selection) server-out storage-out])
         :server   server-in
         :storage  storage-in}))

    om/IWillMount
    (will-mount [_]
      (let [commands (om/get-state owner :commands)
            server   (om/get-state owner :server)
            storage  (om/get-state owner :storage)]
        (lookup-language state document.title server)
        (put! storage [:get :wordie-status "wordieEnabled"])
        (go-loop []
          (when-let [[command data] (<! commands)]
            (case command
              :toggle           (toggle-sidebar state)
              :select           (load-definition state (:text data) server)
              :loading-success  (handle-loading-success state data)
              :loading-error    (handle-loading-error state data)
              :storage-get      (handle-storage-read-response state data)
              :message          (handle-internal-message state data)
              :change-tab       (activate-tab state data)
              nil)
            (recur)))))

    om/IRenderState
    (render-state [_ {:keys [commands]}]
      (let [open    (get-in state [:sidebar :open] false)
            enabled (get-in state [:sidebar :enabled] false)]
        (dom/div #js {:className (str "wordie-sidebar" (if open " open" " closed"))}
                 (dom/div #js {:className "wordie-toggle"
                               :onClick   #(on-toggle-click % commands)} "")
                 (if enabled
                   (om/build sidebar-content-component (:main state) {:init-state {:commands commands}})
                   (dom/div #js {:className "wordie-content"}
                            (dom/div #js {:className "wordie-message"}
                                     "Looks like Wordie lookup is disabled."))))))))

