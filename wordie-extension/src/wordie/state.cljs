(ns wordie.state)

(def app-state
  (atom
   {:sidebar
    {:open false}

    :main
    {:status :initial
     :data   nil}}))
