(ns wordie.state)

(def app-state
  (atom
   {:sidebar
    {:open false}

    :main
    {:phrase "-"}}))
