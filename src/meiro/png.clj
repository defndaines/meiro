(ns meiro.png
  "Generate a PNG image of a maze."
  (:import (java.awt Color Graphics2D)
           (java.awt.geom Line2D$Double Rectangle2D$Double
                          Arc2D Arc2D$Double Ellipse2D$Double)
           (java.awt.image BufferedImage)
           (java.lang Math)
           (javax.imageio ImageIO)
           (java.io File)))

(def ^:private cell-size
  "Cell size constant which determines cell width and height in image."
  20)

(def ^:private default-file
  "Default file name to use if none is provided."
  "maze.png")


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
  ([maze] (render maze default-file))
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
  ([maze] (render-masked maze default-file))
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


(defn- square
  "Create a square at `coord` distance from top and left and with sides
  of `length`. This does not draw the square, only creates the object, as it
  forms the bounds of arcs drawn within it."
  [coord length]
  (Rectangle2D$Double. coord coord length length))


(defn render-polar
  "Render a polar grid maze as a PNG image."
  ([maze] (render-polar maze default-file))
  ([maze ^String file-name]
   (let [image-size (* 2 cell-size (count maze))
         img (BufferedImage. (inc image-size) (inc image-size)
                             BufferedImage/TYPE_INT_ARGB)
         graphic (.createGraphics img)
         center (/ image-size 2)]
     (.setColor graphic Color/BLACK)
     (doseq [[y row] (map-indexed vector maze)]
       (when (pos? y)  ; Never render the center cell.
         (doseq [[x cell] (map-indexed vector row)]
           (let [theta (/ (* 2 Math/PI) (count row))
                 inner-radius (* cell-size y)
                 arc-length (/ 360 (count row))
                 outer-radius (* cell-size (inc y))]
             (when (not-any? #{:inward} cell)
               (let [bounds (square (- center inner-radius) (* 2 inner-radius))
                     start (- 360 arc-length (* x arc-length))]
                 (.draw graphic
                        (Arc2D$Double. bounds start arc-length Arc2D/OPEN))))
             (when (not-any? #{:clockwise} cell)
               (let [theta-cw (* theta (inc x))
                     cx (+ center (* inner-radius (Math/cos theta-cw)))
                     cy (+ center (* inner-radius (Math/sin theta-cw)))
                     dx (+ center (* outer-radius (Math/cos theta-cw)))
                     dy (+ center (* outer-radius (Math/sin theta-cw)))]
                 (.draw graphic (Line2D$Double. cx cy dx dy))))))))
     (.draw graphic (Ellipse2D$Double. 0 0 image-size image-size))
     (ImageIO/write img "png" (File. file-name)))))


(defn render-hex
  "Render a sigma (hex) maze as a PNG image."
  ([maze] (render-hex maze default-file))
  ([maze ^String file-name]
   (let [size 10
         a-size (/ size 2.0)
         b-size (/ (* size (Math/sqrt 3)) 2.0)
         width (* size 2)
         height (* b-size 2)
         rows (count maze)
         columns (count (first maze))
         img-width (inc (+ (* 3 a-size columns) a-size 0.5))
         img-height (inc (+ (* height rows) b-size 0.5))
         img (BufferedImage. img-width img-height
                             BufferedImage/TYPE_INT_ARGB)
         graphic (.createGraphics img)
         draw-line (fn [x y x' y'] (.draw graphic (Line2D$Double. x y x' y')))]
     (.setColor graphic Color/BLACK)
     (doseq [[y row] (map-indexed vector maze)]
       (doseq [[x cell] (map-indexed vector row)]
         (if (not-any? #{:mask} cell)
           (let [cx (+ size (* 3 x a-size))
                 cy (+ b-size (* y height) (if (odd? x) b-size 0))
                 x-far-west (- cx size)
                 x-near-west (- cx a-size)
                 x-near-east (+ cx a-size)
                 x-far-east (+ cx size)
                 y-near (- cy b-size)
                 y-s (+ cy b-size)]
             (when (not-any? #{:north} cell)
               (draw-line x-near-west y-near x-near-east y-near))
             (when (not-any? #{:south} cell)
               (draw-line x-near-east y-s x-near-west y-s))
             (when (not-any? #{:northwest} cell)
               (draw-line x-far-west cy x-near-west y-near))
             (when (not-any? #{:southwest} cell)
               (draw-line x-far-west cy x-near-west y-s))
             (when (not-any? #{:northeast} cell)
               (draw-line x-near-east y-near x-far-east cy))
             (when (not-any? #{:southeast} cell)
               (draw-line x-far-east cy x-near-east y-s))))))
     (ImageIO/write img "png" (File. file-name)))))
