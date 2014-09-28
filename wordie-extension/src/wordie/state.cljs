(ns wordie.state)

(def app-state
  (atom
   {:sidebar
    {:open    false
     :enabled false}

    :main
    {:status   :initial
     :data     nil
     :language nil}}))
