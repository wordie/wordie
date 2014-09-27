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
  [response]
  (try
    [:loading-success (reader/read-string response)]
    (catch :default e
      [:loading-error nil])))

(defn send-request!
  [url responses]
  (goog.net.XhrIo/send url (fn [response]
                    (let [xhr (aget response "target")]
                      (if (.isSuccess xhr)
                        (put! responses (safe-read-response (.getResponseText xhr)))
                        (put! responses [:loading-error   nil]))))))

(defn server-channel
  []
  (let [in  (chan)
        out (chan)]
    (go (loop [url (<! in)]
          (when url
            (send-request! url out)
            (recur (<! in)))))
    {:in in :out out}))
