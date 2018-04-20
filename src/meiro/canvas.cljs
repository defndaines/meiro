(ns meiro.canvas
  "Functions for drawing mazes onto a canvas object."
  (require [clojure.spec.alpha :as spec]))


(def cell-size
  "Cell size constant which determines cell width and height in image."
  15)

(defn draw-line
  "Draw line on canvas from coordinates [x y] to [x' y']."
  [context x y x' y']
  (let [[x y x' y'] (map (partial * cell-size) [x y x' y'])]
    (.moveTo context x y)
    (.lineTo context x' y'))
  (.stroke context))


(spec/fdef line-fn
  :args (spec/cat :context any? :x nat-int? :x' nat-int? :y nat-int? :y' nat-int?)
  :ret nil?)


(defn scaled-line-fn
  "Create a function which will draw a line to scale. Returned function will
  expect that a distance of 1 is a single cell unit, and will use the factor
  to increase the final size when drawn."
  [factor]
  (fn [context x y x' y']
    (let [[x y x' y'] (map (partial * factor) [x y x' y'])]
      (.moveTo context x y)
      (.lineTo context x' y'))
    (.stroke context)))


(defn render
  "Render a `maze` into the provided `context`, using the `draw-fn` to draw
  line edges. Caller should have already set up the context to the appropriate
  dimensions and prepared to be 'drawn' into. This function only draws edges,
  top left to bottom right, and does not render 'cells'. This function will
  not 'scale' the image; it expects the `draw-fn` to handle this."
  [maze context draw-fn]
  (let [rows (count maze)
        cols (count (first maze))]
    (draw-fn context 0 0 0 rows)
    (draw-fn context 0 0 cols 0)
    (doseq [[y row] (map-indexed vector maze)]
      (doseq [[x cell] (map-indexed vector row)]
        (when (not-any? #{:east} cell)
          (draw-fn context (inc x) y (inc x) (inc y)))
        (when (not-any? #{:south} cell)
          (draw-fn context x (inc y) (inc x) (inc y)))))))

(comment
  (def canvas (.getElementById js/document "drawing"))
  (def context (.getContext canvas "2d"))
  (render maze context (scaled-line-fn cell-size)))
