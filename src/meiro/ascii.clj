(ns meiro.ascii)

(def ^:private corner "+")
(def ^:private horizontal-wall "---")
(def ^:private verticle-wall "|")
(def ^:private inside-cell "   ")
(def ^:private verticle-link " ")
(def ^:private horizontal-link "   ")

(defn- top-level [maze]
  (apply str
         (flatten
           (concat corner
                   (repeat (count (first maze)) (concat horizontal-wall corner))
                   "\n"))))

(defn- cell-level
  ([cell] (cell-level cell inside-cell))
  ([cell inside]
   (concat inside
           (if (some #{:east} cell) verticle-link verticle-wall))))

(defn- bottom-level [cell]
  (concat (if (some #{:south} cell) horizontal-link horizontal-wall) corner))

(defn render
  "Render a maze as ASCII art."
  [maze]
  (apply str
         (top-level maze)
         (mapcat
           (fn [row]
             (concat verticle-wall (mapcat cell-level row) "\n"
                     corner (mapcat bottom-level row) "\n"))
           maze)))

(defn render-distances
  "Render a maze with the distance values in each cell.
  Uses base-36 to avoid spacing issues in smaller mazes."
  [maze distances]
  (apply str
         (top-level maze)
         (flatten
           (for [row (range (count maze))]
             (concat
               verticle-wall
               (for [col (range (count (first maze)))]
                 (cell-level (get-in maze [row col])
                             (str \space (Integer/toString (get-in distances [row col]) 36) \space)))
               "\n"
               corner
               (for [col (range (count (first maze)))]
                 (bottom-level (get-in maze [row col])))
               "\n")))))
