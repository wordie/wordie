(ns wordie.events
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as async :refer [chan put! sliding-buffer <!]]
            [cljs.reader :as reader]
            [goog.events :as events]
            [goog.net.XhrIo]))

(defn get-selection
  []
  (when-let [selection (.getSelection js/window)]
    (let [s (.trim (str selection))]
      (when (seq s)
        s))))

(defn- handle-text-selection
  [e ch]
  (when-let [selection (get-selection)]
    (put! ch [:select {:text selection
                       :x    (.-pageX e)
                       :y    (.-pageY e)}])))

(defn selection
  []
  (let [ch (chan (sliding-buffer 1))]
    (events/listen js/document events/EventType.MOUSEUP #(handle-text-selection % ch))
    ch))

(defn- safe-read-response
  [event-type response]
  (try
    [:loading-success [event-type (reader/read-string response)]]
    (catch :default e
      [:loading-error [event-type nil]])))

(defn send-request!
  [[event-type url] responses]
  (goog.net.XhrIo/send url (fn [response]
                             (let [xhr (aget response "target")]
                               (if (.isSuccess xhr)
                                 (put! responses (safe-read-response event-type (.getResponseText xhr)))
                                 (put! responses [:loading-error [event-type nil]]))))))

(defn server-channel
  []
  (let [in  (chan)
        out (chan)]
    (go (loop [request (<! in)]
          (when request
            (send-request! request out)
            (recur (<! in)))))
    {:in in :out out}))

(defn make-storage-api-request!
  [[type callback data] responses]
  (let [storage (.. js/chrome -storage -local)
        request (clj->js data)]
    (case type
      :get (.get storage request (fn [response]
                                   (put! responses [:storage-get [callback (js->clj response)]])))
      :set (.set storage request (fn []
                                   (put! responses [:storage-set [callback nil]])))
      nil)))

(defn storage-channel
  []
  (let [in  (chan)
        out (chan)]
    (go (loop [request (<! in)]
          (when request
            (make-storage-api-request! request out)
            (recur (<! in)))))
    {:in in :out out}))
