(ns wordie.state)

(def app-state
  (atom
   {:sidebar
    {:open    false
     :enabled false}

    :main
    {:tab     :definition
     :language   nil
     :phrase     nil

     :definition
     {:data   nil
      :status :initial}

     :thesaurus
     {:data   nil
      :status :initial}

     :wikipedia
     {:data   nil
      :status :initial}
     }}))
