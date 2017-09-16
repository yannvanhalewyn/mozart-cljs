;; A graph utility. These are simple functions that allows adding and
;; connecting nodes to a graph data structure. The structure itself is
;; nothing more than a map where the keys are the nodes and the values
;; a vector of graph edges (ie: other nodes).
;;
;; Example:

;; +---+
;; | A +-----------+
;; +-+-+           |
;;   |             |
;;   |             v
;;   |           +---+
;;   |           | C |
;;   |           +---+
;;   |             ^
;;   v             |
;; +---+           |
;; | B +-----------+
;; +---+
;;
;; Will be represented by the map:
;; `{A '(B C)
;;   B '(C)
;;   C '()}`
(ns mozart.graph)

(defn- add-node
  "Adds a node to the graph if not present yet."
  [graph node]
  (if (get graph node)
    graph
    (assoc graph node (list))))

(defn connect
  "Will connect from to to in the graph"
  [graph from to]
  (-> (add-node graph to)
      (update from conj to)))

(defn connect->
  ""
  [graph & nodes]
  (let [[from to & remaining] nodes]
    (if (empty? remaining)
      (connect graph from to)
      (apply connect-> (connect graph from to) (rest nodes)))))

(defn connected?
  "Finds out wether or not a node is connected to another node in the graph."
  [graph node1 node2]
  (some #(= node2 %) (get graph node1)))
