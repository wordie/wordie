(ns wordie.events
  (:require [cljs.core.async :as async :refer [chan put! sliding-buffer <!]]
            [goog.events :as events]))

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

