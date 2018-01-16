(ns meiro.nethack
  "Generate a NetHack-style ASCII representation of a maze."
  (:require [clojure.string :as string]))


(def ^:private horizontal-wall "-")
(def ^:private verticle-wall "|")
(def ^:private inside-cell ".")
(def ^:private cell-link ".")
; (def ^:private start-cell "@"
; (def ^:private end-cell "$")
(def ^:private corridor "#")
(def ^:private corridor-wall " ")


(defn- top-level
  "Render the top edge of the maze."
  [maze]
  (string/join
    (repeat
      (inc (* 2 (count (first maze))))
      horizontal-wall)))


(defn- cell-room-level
  "Render the cell level, i.e., where the 'inside' of the cell is displayed."
  ([cell] (cell-room-level cell inside-cell))
  ([cell inside]
   (concat inside
           (if (some #{:east} cell) cell-link verticle-wall))))


(defn- bottom-room-level
  "Render the bottom edge of a cell, or precisely the south and south-east
  edge."
  [cell]
  (concat
    (if (some #{:south} cell) cell-link horizontal-wall)
    (if (some #{:south} cell)
      (if (not-any? #{:east} cell) verticle-wall horizontal-wall)
      horizontal-wall)))


(defn render-room
  "Render a maze in NetHack style as if it was the interior of a room."
  ([maze]
   (apply str
          (top-level maze) \newline
          (mapcat
            (fn [row]
              (concat verticle-wall (mapcat cell-room-level row) "\n"
                      verticle-wall (mapcat bottom-room-level row) "\n"))
            maze))))


(defn cell-corridor-level
  "Render the cell level, i.e., where the 'inside of the cell is displayed."
  [cell]
  (concat corridor
          (if (some #{:east} cell) corridor corridor-wall)))


(defn bottom-corridor-level
  "Render the bottom edge of a cell, or precisely the south and south-east
  edge."
  [cell]
  (concat
    (if (some #{:south} cell) corridor corridor-wall)
    corridor-wall))


(defn render-corridor
  "Render a maze in NetHack style as if it was a series of corridors."
  [maze]
  (clojure.string/join
         (mapcat
           (fn [row]
             (concat (mapcat cell-corridor-level row) "\n"
                     (mapcat bottom-corridor-level row) "\n"))
           maze)))
