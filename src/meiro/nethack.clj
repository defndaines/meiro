(ns meiro.nethack
  "Generate a NetHack-style ASCII representation of a maze."
  (:require [clojure.string :as string]))


(def ^:private horizontal-wall "-")
(def ^:private verticle-wall "|")
(def ^:private inside-cell ".")
(def ^:private cell-link ".")
(def ^:private start-cell "@")
(def ^:private end-cell "$")


(defn- top-level
  "Render the top edge of the maze."
  [maze]
  (string/join
    (repeat
      (inc (* 2 (count (first maze))))
      horizontal-wall)))


(defn- cell-level
  "Render the cell level, i.e., where the 'inside' of the cell is displayed."
  ([cell] (cell-level cell inside-cell))
  ([cell inside]
   (concat inside
           (if (some #{:east} cell) cell-link verticle-wall))))


(defn- bottom-level
  "Render the bottom edge of a cell, or precisely the south and south-east
  edge."
  [cell]
  (concat
    (if (some #{:south} cell) cell-link horizontal-wall)
    horizontal-wall))


(defn render
  "Render a maze in NetHack style. Uses the cell-fn if provided."
  ([maze]
   (apply str
          (top-level maze) \newline
          (mapcat
            (fn [row]
              (concat verticle-wall (mapcat cell-level row) "\n"
                      verticle-wall (mapcat bottom-level row) "\n"))
            maze))))
