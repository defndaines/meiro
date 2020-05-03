(ns meiro.ascii
  "Generate an ASCII representation of a maze. Useful for debugging."
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(def ^:private corner "+")
(def ^:private horizontal-wall "---")
(def ^:private verticle-wall "|")
(def ^:private inside-cell "   ")
(def ^:private verticle-link " ")
(def ^:private horizontal-link "   ")

(defn- top-level
  "Render the top edge of the maze."
  [maze]
  (string/join
    (flatten
      (concat
        corner
        (repeat (count (first maze)) (concat horizontal-wall corner))
        "\n"))))

(defn- cell-level
  "Render the cell level, i.e., where the 'inside' of the cell is displayed.
  This may contain values, or will default to just empty space."
  ([cell] (cell-level cell inside-cell))
  ([cell inside]
   (concat inside
           (if (some #{:east} cell) verticle-link verticle-wall))))

(defn- bottom-level
  "Render the bottom edge of a cell, or precisely the south and south-east
  edge."
  [cell]
  (concat (if (some #{:south} cell) horizontal-link horizontal-wall) corner))

(defn render
  "Render a maze as ASCII art. Uses the cell-fn if provided."
  ([maze]
   (apply str
          (top-level maze)
          (mapcat
            (fn [row]
              (concat verticle-wall (mapcat cell-level row) "\n"
                      corner (mapcat bottom-level row) "\n"))
            maze)))
  ;; Could just call (render maze (fn [_] inside-cell)), but feel like there's
  ;; a more elegant solution hiding here which I'll eventually figure out.
  ([maze cell-fn]
   (apply str
          (top-level maze)
          (flatten
            (for [row (range (count maze))]
              (concat
                verticle-wall
                (for [col (range (count (first maze)))]
                  (cell-level (get-in maze [row col])
                              (cell-fn [row col])))
                "\n"
                corner
                (for [col (range (count (first maze)))]
                  (bottom-level (get-in maze [row col])))
                "\n"))))))

(defn show-distance
  "Auxiliary function for rendering distance values inside a cell.
  Uses base-36 to avoid spacing issues in smaller mazes."
  [distances]
  (fn [cell]
    (str \space (Integer/toString (get-in distances cell) 36) \space)))

(defn show-solution
  "Auxiliary function for rendering the solution to a maze."
  [solution]
  (fn [cell]
    (str \space (if (some #{cell} solution) \* \space) \space)))

(defn line-to-row
  "Convert line of 'x' and '.' to a grid row with masked cells."
  [line]
  (vec (map #(if (= \. %) [] [:mask]) line)))

(defn read-grid
  "Read in an ASCII grid using 'x' to mark masked cells and '.' for open
  cells."
  [file-name]
  (with-open [reader (io/reader file-name)]
    (vec (map line-to-row (line-seq reader)))))
