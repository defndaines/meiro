(ns meiro.png
  "Generate a PNG image of a maze."
  (:import (java.awt Color Graphics2D)
           (java.awt.image BufferedImage)
           (javax.imageio ImageIO)
           (java.io File)))

(def ^:private cell-size
  "Cell size constant which determines cell width and height in image."
  25)

(defn- draw
  "Draw line in graphic from coordinates."
  [^Graphics2D graphic x y x' y']
  (.drawLine graphic
             (* cell-size x)
             (* cell-size y)
             (* cell-size x')
             (* cell-size y')))

(defn render
  "Render a maze as a PNG image."
  ([maze] (render maze "maze.png"))
  ([maze ^String file-name]
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
       (doseq [[x cell] (map-indexed vector row)]
         (when (not-any? #{:east} cell)
           (draw graphic (inc x) y (inc x) (inc y)))
         (when (not-any? #{:south} cell)
           (draw graphic x (inc y) (inc x) (inc y)))))
     (ImageIO/write img "png" (File. file-name)))))

(defn render-masked
  "Render a maze as a PNG image, but not printing masked cells."
  ([maze] (render-masked maze "maze.png"))
  ([maze ^String file-name]
   (let [rows (count maze)
         cols (count (first maze))
         img (BufferedImage.
               (inc (* cell-size cols))
               (inc (* cell-size rows))
               BufferedImage/TYPE_INT_ARGB)
         graphic (.createGraphics img)]
     (.setColor graphic Color/BLACK)
     (doseq [[y row] (map-indexed vector maze)]
       (doseq [[x cell] (map-indexed vector row)]
         (if (not-any? #{:mask} cell)
           (let [x+ (inc x)
                 y+ (inc y)]
             (when (not-any? #{:north} cell)
               (draw graphic x y x+ y))
             (when (not-any? #{:west} cell)
               (draw graphic x y x y+))
             (when (not-any? #{:east} cell)
               (draw graphic x+ y x+ y+))
             (when (not-any? #{:south} cell)
               (draw graphic x y+ x+ y+))))))
     (ImageIO/write img "png" (File. file-name)))))
