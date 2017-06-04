(ns meiro.png
  "Generate a PNG image of a maze."
  (:import (java.awt Color Graphics2D)
           (java.awt.geom Line2D$Double Ellipse2D$Double)
           (java.awt.image BufferedImage)
           (java.lang Math)
           (javax.imageio ImageIO)
           (java.io File)))

(def ^:private cell-size
  "Cell size constant which determines cell width and height in image."
  20)


(defn- draw
  "Draw line in graphic from coordinates."
  [^Graphics2D graphic x y x' y']
  (.draw graphic
         (Line2D$Double.
           (* cell-size x)
           (* cell-size y)
           (* cell-size x')
           (* cell-size y'))))


;; This method is optimized not to draw a line more than once.
;; Because of this, it has to assume a rectangular grid with no masking.

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


(defn render-polar
  "Render a polar grid maze as a PNG image."
  ([maze] (render-polar maze "maze.png"))
  ([maze ^String file-name]
   (let [rows (count maze)
         image-size (* 2 cell-size rows)
         img (BufferedImage. (inc image-size) (inc image-size) BufferedImage/TYPE_INT_ARGB)
         graphic (.createGraphics img)
         center (/ image-size 2)]
     (.setColor graphic Color/BLACK)
     (doseq [[y row] (map-indexed vector maze)]
       (doseq [[x cell] (map-indexed vector row)]
         (let [theta (/ (* 2 Math/PI) (count row))
               inner-radius (* cell-size y)
               outer-radius (* cell-size (inc y))
               theta-ccw (* theta x)
               theta-cw (* theta (inc x))
               cx (+ center (* inner-radius (Math/cos theta-cw)))
               cy (+ center (* inner-radius (Math/sin theta-cw)))]
           (when (not-any? #{:north} cell)
             (.draw graphic
                    (Line2D$Double.
                      (+ center (* inner-radius (Math/cos theta-ccw)))
                      (+ center (* inner-radius (Math/sin theta-ccw)))
                      cx cy)))
           (when (not-any? #{:east} cell)
             (.draw graphic
                    (Line2D$Double.
                      cx cy
                      (+ center (* outer-radius (Math/cos theta-cw)))
                      (+ center (* outer-radius (Math/sin theta-cw)))))))))
     (.draw graphic (Ellipse2D$Double. 0 0 image-size image-size))
     (ImageIO/write img "png" (File. file-name)))))
