(ns daw.graph)

(defn- add-node [graph node]
  (if (get graph node)
    graph
    (assoc graph node '())))

(defn connect [graph from to]
  (-> (add-node graph to)
      (update  from conj to)))

(defn connect-> [graph & nodes]
  (let [[from to & remaining] nodes]
    (if (empty? remaining)
      (connect graph from to)
      (apply connect-> (connect graph from to) (rest nodes)))))
