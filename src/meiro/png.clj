(ns meiro.png
  "Generate a PNG image of a maze."
  (:require [meiro.weave :as weave])
  (:import (java.awt Color Graphics2D)
           (java.awt.geom Line2D$Double Rectangle2D$Double
                          Arc2D Arc2D$Double Ellipse2D$Double)
           (java.awt.image BufferedImage)
           (java.lang Math)
           (javax.imageio ImageIO)
           (java.io File)))


;; NOTE: All the grid logic at this point is built up using [row column].
;;  This means that positions, when mapped to coordinates, are flipped from
;;  what one might normally expect, i.e., [y x] instead of [x y].
;;  "Fixing" this would break a lot, so I may not refactor this.


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


(defn- draw-line
  "Draw a line using float-based coordinates."
  [^Graphics2D graphic x y x' y']
  (.draw graphic (Line2D$Double. x y x' y')))


(defn- render-cells
  "Use a function to render each cell into a PNG image.

  This function extracts out the common rendering pattern for PNGs.
  Most rendering cares only about rendering one cell at a time, and can do
  that with the four arguments passed: graphic, x, y, cell.
  Since masking prevent cell rendering, it is integrated in here."
  ([maze file-name img-width img-height cell-fn]
   (let [img (BufferedImage. img-width img-height
                             BufferedImage/TYPE_INT_ARGB)
         graphic (.createGraphics img)]
     (.setColor graphic Color/BLACK)
     (doseq [[y row] (map-indexed vector maze)]
       (doseq [[x cell] (map-indexed vector row)]
         (when (not-any? #{:mask} cell)
           (cell-fn graphic x y cell))))
     (ImageIO/write img "png" (File. file-name)))))


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
   (render-cells
     maze file-name
     (inc (* cell-size (count (first maze))))
     (inc (* cell-size (count maze)))
     (fn [graphic x y cell]
       (let [x+ (inc x)
             y+ (inc y)]
         (when (not-any? #{:north} cell)
           (draw graphic x y x+ y))
         (when (not-any? #{:west} cell)
           (draw graphic x y x y+))
         (when (not-any? #{:east} cell)
           (draw graphic x+ y x+ y+))
         (when (not-any? #{:south} cell)
           (draw graphic x y+ x+ y+)))))))


(defn- square
  "Create a square at `coord` distance from top and left and with sides
  of `length`. This does not draw the square, only creates the object, as it
  forms the bounds of arcs drawn within it."
  [coord length]
  (Rectangle2D$Double. coord coord length length))


(defn render-polar
  "Render a polar grid maze as a PNG image."
  ;; Doesn't use render-cells because outside circle is drawn once.
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
   (let [size cell-size
         a-size (/ size 2.0)
         b-size (/ (* size (Math/sqrt 3)) 2.0)
         height (* b-size 2)
         rows (count maze)
         columns (count (first maze))
         img-width (inc (+ (* 3 a-size columns) a-size 0.5))
         img-height (inc (+ (* height rows) b-size 0.5))]
     (render-cells
       maze file-name
       img-width img-height
       (fn [graphic x y cell]
         (let [cx (+ size (* 3 x a-size))
               cy (+ b-size (* y height) (if (odd? x) b-size 0))
               x-far-west (- cx size)
               x-near-west (- cx a-size)
               x-near-east (+ cx a-size)
               x-far-east (+ cx size)
               y-near (- cy b-size)
               y-s (+ cy b-size)]
           (when (not-any? #{:north} cell)
             (draw-line graphic x-near-west y-near x-near-east y-near))
           (when (not-any? #{:south} cell)
             (draw-line graphic x-near-east y-s x-near-west y-s))
           (when (not-any? #{:northwest} cell)
             (draw-line graphic x-far-west cy x-near-west y-near))
           (when (not-any? #{:southwest} cell)
             (draw-line graphic x-far-west cy x-near-west y-s))
           (when (not-any? #{:northeast} cell)
             (draw-line graphic x-near-east y-near x-far-east cy))
           (when (not-any? #{:southeast} cell)
             (draw-line graphic x-far-east cy x-near-east y-s))))))))


(defn render-delta
  "Render a delta (triangle) maze as a PNG image."
  ([maze] (render-delta maze default-file))
  ([maze file-name]
   (let [size cell-size
         half-width  (/ size 2.0)
         height (/ (* size (Math/sqrt 3)) 2.0)
         half-height (/ height 2)
         rows (count maze)
         columns (count (first maze))
         img-width (inc (/ (* size (inc columns)) 2))
         img-height (inc (* height rows))]
     (render-cells
       maze file-name
       img-width img-height
       (fn [graphic x y cell]
         (let [cx (+ half-width (* x half-width))
               cy (+ half-height (* y height))
               west-x (- cx half-width)
               east-x (+ cx half-width)
               upright? (even? (+ x y))
               apex-y ((if upright? - +) cy half-height)
               base-y ((if upright? + -) cy half-height)]
           (when (not-any? #{:west} cell)
             (draw-line graphic west-x base-y cx apex-y))
           (when (not-any? #{:east} cell)
             (draw-line graphic east-x base-y cx apex-y))
           (when (not-any? #{:north :south} cell)
             (draw-line graphic east-x base-y west-x base-y))))))))


(defn- coordinates-with-inset
  "Derive the eight coordinates needed for rendering insets.
  [x1 x2 x3 x4 y1 y2 y3 y4]"
  [x y size inset]
  (let [x1 (* x size)
        y1 (* y size)
        x4 (+ x1 size)
        y4 (+ y1 size)]
    [x1 (+ x1 inset) (- x4 inset) x4
     y1 (+ y1 inset) (- y4 inset) y4]))


(defn- open?
  "Does the given cell link to a cell in the provided direction, either directly
  or by passing underneath."
  [dir x y cell]
  (or (some #{dir} cell)
      (some #{dir} (map #(weave/direction [y x] %)
                         (filter vector? cell)))))

(defn- link-north
  "Draw inset link to the cell to the north."
  [graphic [_ x2 x3 _ y1 y2 _ _]]
  (draw-line graphic x2 y1 x2 y2)
  (draw-line graphic x3 y1 x3 y2))


(defn- link-south
  "Draw inset link to the cell to the south."
  [graphic [_ x2 x3 _ _ _ y3 y4]]
  (draw-line graphic x2 y3 x2 y4)
  (draw-line graphic x3 y3 x3 y4))


(defn- link-east
  "Draw inset link to the cell to the east."
  [graphic [_ _ x3 x4 _ y2 y3 _]]
  (draw-line graphic x3 y2 x4 y2)
  (draw-line graphic x3 y3 x4 y3))


(defn- link-west
  "Draw inset link to the cell to the west"
  [graphic [x1 x2 _ _ _ y2 y3 _]]
  (draw-line graphic x1 y2 x2 y2)
  (draw-line graphic x1 y3 x2 y3))


(defn render-inset
  "Render a maze to PNG with insets.
  Inset mazes will handle weave mazes by default."
  ([maze inset] (render-inset maze default-file inset))
  ([maze ^String file-name inset]
   (render-cells
     maze file-name
     (inc (* cell-size (count (first maze))))
     (inc (* cell-size (count maze)))
     (fn [graphic x y cell]
       (let [[_ x2 x3 _ _ y2 y3 _ :as coords]
             (coordinates-with-inset x y cell-size inset)]
         (if (open? :north x y cell)
           (link-north graphic coords)
           (draw-line graphic x2 y2 x3 y2))
         (if (open? :west x y cell)
           (link-west graphic coords)
           (draw-line graphic x2 y2 x2 y3))
         (if (open? :east x y cell)
           (link-east graphic coords)
           (draw-line graphic x3 y2 x3 y3))
         (if (open? :south x y cell)
           (link-south graphic coords)
           (draw-line graphic x2 y3 x3 y3))
         (when (some #{:under} cell)
           (if (some #{:north} cell)
             (do
               (link-east graphic coords)
               (link-west graphic coords))
             (do
               (link-north graphic coords)
               (link-south graphic coords)))))))))


(defn- fill-square
  [graphic x y]
  (let [x' (inc (* x cell-size))
        y' (inc (* y cell-size))
        length (- cell-size 2)]
    (.fill graphic
           (Rectangle2D$Double. x' y' length length))))


(defn- fill-passage
  [graphic x y x' y']
  (let [start-x (inc (* x cell-size))
        start-y (inc (* y cell-size))
        length-x (+ cell-size (- (* x' cell-size) start-x 1))
        length-y (+ cell-size (- (* y' cell-size) start-y 1))]
    (.fill graphic
           (Rectangle2D$Double. start-x start-y length-x length-y))))


(defn render-forest
  "Render a forest to PNG.
  Because forests are a collection of edges, other render functions (which are
  position-aware) cannot render them without conversion. To work around that,
  this function bores out the maze by iterating through the edges whereever they
  are."
  [forest]
  (let [{:keys [width height edges]} forest
        img-width (* cell-size width)
        img-height (* cell-size height)
        img (BufferedImage. img-width img-height
                            BufferedImage/TYPE_INT_ARGB)
        graphic (.createGraphics img)]
    ;; Color entire image black.
    (.setColor graphic Color/BLACK)
    (.fill graphic (Rectangle2D$Double. 0 0 img-width img-height))
    ;; Then bore out cells and passages with white.
    (.setColor graphic Color/WHITE)
    (doseq [[[x y] [x' y']] edges]
      (fill-square graphic x y)
      (fill-square graphic x' y')
      (fill-passage graphic x y x' y'))
    (ImageIO/write img "png" (File. default-file))))


(defn- draw-up-arrow
  "Draw an up arrow (pointing right)."
  [^Graphics2D graphic x y]
  (let [offset 4
        half-x (+ (* cell-size x) (/ cell-size 2) 1)
        x' (- (* cell-size (inc x)) offset)
        half-y (+ (* cell-size y) (/ cell-size 2))]
    (.draw graphic
           (Line2D$Double.
             half-x (+ (* cell-size y) offset)
             x' half-y))
    (.draw graphic
           (Line2D$Double.
             x' half-y
             half-x (- (* cell-size (inc y)) offset)))))


(defn- draw-down-arrow
  "Draw a down arrow (pointing left)."
  [^Graphics2D graphic x y]
  (let [offset 4
        half-x (dec (+ (* cell-size x) (/ cell-size 2)))
        x' (+ (* cell-size x) offset)
        half-y (+ (* cell-size y) (/ cell-size 2))]
    (.draw graphic
           (Line2D$Double.
             half-x (+ (* cell-size y) offset)
             x' half-y))
    (.draw graphic
           (Line2D$Double.
             x' half-y
             half-x (- (* cell-size (inc y)) offset)))))


(defn render-3d
  "Lay out a three-dimensional maze with each level side by side. Uses arrows to
  indicate when to move between levels."
  [maze]
  (let [levels (count maze)
        height (count (first maze))
        width (count (ffirst maze))
        gap-size (/ cell-size 2)
        img-width (+ (* gap-size levels)
                     (* levels (* cell-size width)))
        img-height (inc (* cell-size height))
        img (BufferedImage. img-width img-height
                            BufferedImage/TYPE_INT_ARGB)
        graphic (.createGraphics img)]
    (.setColor graphic Color/BLACK)
    (doseq [[z level] (map-indexed vector maze)]
      (let [offset (+ (* z 0.5) (* z width))]
        (doseq [[y row] (map-indexed vector level)]
          (doseq [[x' cell] (map-indexed vector row)]
            (when (not-any? #{:mask} cell)
              (let [x (+ offset x')
                    x+ (inc x)
                    y+ (inc y)]
                (when (not-any? #{:north} cell)
                  (draw graphic x y x+ y))
                (when (not-any? #{:west} cell)
                  (draw graphic x y x y+))
                (when (not-any? #{:east} cell)
                  (draw graphic x+ y x+ y+))
                (when (not-any? #{:south} cell)
                  (draw graphic x y+ x+ y+))
                (when (some #{:up} cell)
                  (draw-up-arrow graphic x y))
                (when (some #{:down} cell)
                  (draw-down-arrow graphic x y))))))))
    (ImageIO/write img "png" (File. default-file))))
