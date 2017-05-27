(ns meiro.unicode
  "Unicode rendering of a maze.

  NOTE: Not pretty. This is super clunky, since all maze edges are treated
        exceptionally. I don't intend to revist this code to clean it up."
  (:require [meiro.core :as m]))

(def ^:private corner "\u254b")
(def ^:private nw-corner "\u250f")
(def ^:private ne-corner "\u2513")
(def ^:private sw-corner "\u2517")
(def ^:private se-corner "\u251b")
(def ^:private nes-tee "\u2523")
(def ^:private nws-tee "\u252b")
(def ^:private wse-tee "\u2533")
(def ^:private wne-tee "\u253b")
(def ^:private ew-edge "\u2501")
;; These are the right size, but don't align correctly.
; (def ^:private n-edge "\u2579")
; (def ^:private s-edge "\u257b")
; (def ^:private e-edge "\u257a")
; (def ^:private w-edge "\u2578")
(def ^:private n-edge "\u2503")
(def ^:private s-edge "\u2503")
(def ^:private e-edge "\u2501")
(def ^:private w-edge "\u2501")
(def ^:private horizontal-wall "\u2501\u2501")
(def ^:private verticle-wall "\u2503")
(def ^:private inside-cell "  ")
(def ^:private verticle-link " ")
(def ^:private horizontal-link "  ")

(defn- top-level [maze]
  (apply str
         (flatten
           (concat
             nw-corner
             (map
               #(concat horizontal-wall (if (some #{:east} %) ew-edge wse-tee))
               (butlast (first maze)))
             horizontal-wall ne-corner
             "\n"))))

(defn- cell-level [cell]
  (concat inside-cell
          (if (some #{:east} cell) verticle-link verticle-wall)))

(defn- bottom-row
  "Get south and south-east characters for a given row."
  [maze cell]
  (case [(some #{:south} (get-in maze cell))
         (some #{:east} (get-in maze cell))
         (some #{:east} (get-in maze (m/south cell)))
         (some #{:south} (get-in maze (m/east cell)))]
    [:south :east :east nil] [horizontal-link e-edge]
    [:south :east nil :south] [horizontal-link s-edge]
    [:south nil :east :south] [horizontal-link n-edge]
    [nil :east :east :south] [horizontal-wall w-edge]
    [nil :east :east nil] [horizontal-wall ew-edge]
    [:south nil nil :south] [horizontal-link verticle-wall]
    [:south :east nil nil] [horizontal-link nw-corner]
    [nil :east nil :south] [horizontal-wall ne-corner]
    [nil nil :east :south] [horizontal-wall se-corner]
    [:south nil :east nil] [horizontal-link sw-corner]
    [:south nil nil nil] [horizontal-link nes-tee]
    [nil nil nil :south] [horizontal-wall nws-tee]
    [nil :east nil nil] [horizontal-wall wse-tee]
    [nil nil :east nil] [horizontal-wall wne-tee]
    [nil nil nil nil] [horizontal-wall corner]))

(defn- bottom-left
  [maze cell]
  (if (some #{:south} (get-in maze cell))
    verticle-wall
    nes-tee))

(defn- bottom-last
  [maze cell]
  (if (some #{:south} (get-in maze cell))
    [horizontal-link verticle-wall]
    [horizontal-wall nws-tee]))

(defn- last-row
  [row]
  (flatten
    (concat
      verticle-wall
      (map cell-level row)
      "\n"
      sw-corner
      (map (fn [cell]
             [horizontal-wall
              (if (some #{:east} cell) verticle-link verticle-wall)])
           (butlast row))
      horizontal-wall se-corner "\n")))

(defn render
  "Render a maze as unicode art."
  [maze]
  (apply
    str
    (concat
      (top-level maze)
      (flatten
        (for [row (range (dec (count maze)))]
          (concat
            verticle-wall
            (for [col (range (count (first maze)))]
              (cell-level (get-in maze [row col])))
            "\n"
            (bottom-left maze [row 0])
            (for [col (range (dec (count (first maze))))]
              (bottom-row maze [row col]))
            (bottom-last maze [row (dec (count (first maze)))])
            "\n")))
      (last-row (last maze)))))
