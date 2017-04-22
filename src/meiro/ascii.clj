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

(defn- cell-level [cell]
  (concat inside-cell
          (if (some #{:east} cell) verticle-link verticle-wall)))

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
