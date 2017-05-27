(ns meiro.png
  "Generate a PNG image of a maze."
  (:import (java.awt Color)
           (java.awt.image BufferedImage)
           (javax.imageio ImageIO)
           (java.io File)))

(def ^:private cell-size
  "Cell size constant which determines cell width and height in image."
  25)

(defn render
  "Render a maze as a PNG image."
  ([maze] (render maze "maze.png"))
  ([maze file-name]
   (let [rows (count maze)
         cols (count (first maze))
         img (BufferedImage.
               (inc (* cell-size cols))
               (inc (* cell-size rows))
               BufferedImage/TYPE_INT_ARGB)
         graphic (.createGraphics img)]
     (.setColor graphic Color/BLACK)
     (.drawLine graphic 0 0 0 (* cell-size rows))
     (.drawLine graphic 0 0 (* cell-size cols) 0)
     (doseq [[y row] (map-indexed vector maze)]
       (doseq [[x col] (map-indexed vector row)]
         (when (not-any? #{:east} col)
           (.drawLine graphic
                      (* cell-size (inc x))
                      (* cell-size y)
                      (* cell-size (inc x))
                      (* cell-size (inc y))))
         (when (not-any? #{:south} col)
           (.drawLine graphic
                      (* cell-size x)
                      (* cell-size (inc y))
                      (* cell-size (inc x))
                      (* cell-size (inc y))))))
     (ImageIO/write img "png" (File. file-name)))))
