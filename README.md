# Meiro 迷路

Maze generation code, inspired by working through [Mazes for
Programmers](https://pragprog.com/book/jbmaze/mazes-for-programmers).
Because the book leans on Object Oriented design (coded in Ruby), much of this
is a re-thinking of the approaches in a Clojure style.

Each maze generation algorithm is in its own namespace.


## Usage

Project is currently _under development_ and does not have functions exposed for
external use.


### Displaying Mazes

There are several ways to display a maze. The data structure used to store a
maze is a vector of vectors, where each cell indicates which directions you can
navigate out of the cell to.

Here is a 5x5 maze:
```clojure
[[[:east] [:south :west :east] [:west :east] [:west :south] [:south]]
 [[:east :south] [:east :north :west] [:south :west] [:north :east] [:west :north]]
 [[:north :east] [:west] [:south :north :east] [:west] [:south]]
 [[:south] [:south] [:south :north :east] [:west :east] [:west :north :south]]
 [[:east :north] [:north :west :east] [:west :north] [:east] [:north :west]]]
```

The easiest way to visualize a maze at the REPL is to generate an ASCII
version:
```clojure
user=> (require '[meiro.ascii :as ascii])
nil
user=> (print (ascii/render maze))
+---+---+---+---+---+
|               |   |
+---+   +---+   +   +
|           |       |
+   +---+   +---+---+
|       |       |   |
+---+---+   +---+   +
|   |   |           |
+   +   +   +---+   +
|           |       |
+---+---+---+---+---+
nil
```

And if you want to print or share a maze, it can be output as a PNG:
```clojure
(require '[meiro.png :as png])
(png/render (sw/create (m/init 15 20)) "sample-maze.png")
```
Which creates a PNG file like:

![Sample Maze](sample-maze.png)

To print a maze with masked cells:
```clojure
(def grid (ascii/read-grid "template.txt"))
(png/render-masked (b/create grid))
```

![Masked Maze](masked-maze.png)

To print a circular (polar) maze:
```clojure
(png/render-polar (b/create (m/init 10 36)))
```

![Polar Maze](polar-maze.png)


## Algorithms

There are a number of different algorithms for generating mazes.


### Binary Tree

Binary Tree produces mazes with a bias toward paths which flow down and to the
right. They will always have a single corridor along both the southern and
eastern edges.

If you wish to generate and print a random binary-tree maze, you can start up a
REPL and try to following:
```clojure
(require '[meiro.core :as m])
(require '[meiro.ascii :as ascii])
(require '[meiro.binary-tree :as bt])
(print (ascii/render (bt/create (m/init 8 25))))
```

Which will produce a maze like:
```
+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
|   |           |   |       |           |   |       |   |               |       |   |               |
+   +---+---+   +   +---+   +---+---+   +   +---+   +   +---+---+---+   +---+   +   +---+---+---+   +
|   |               |       |   |       |   |   |   |           |   |   |   |   |   |   |   |       |
+   +---+---+---+   +---+   +   +---+   +   +   +   +---+---+   +   +   +   +   +   +   +   +---+   +
|           |               |           |   |               |       |                   |   |   |   |
+---+---+   +---+---+---+   +---+---+   +   +---+---+---+   +---+   +---+---+---+---+   +   +   +   +
|       |   |               |   |   |           |   |   |   |   |           |   |                   |
+---+   +   +---+---+---+   +   +   +---+---+   +   +   +   +   +---+---+   +   +---+---+---+---+   +
|   |   |           |   |       |           |   |   |               |       |   |               |   |
+   +   +---+---+   +   +---+   +---+---+   +   +   +---+---+---+   +---+   +   +---+---+---+   +   +
|   |       |               |                   |       |   |           |   |   |   |       |       |
+   +---+   +---+---+---+   +---+---+---+---+   +---+   +   +---+---+   +   +   +   +---+   +---+   +
|       |               |   |   |       |   |       |                   |       |   |   |   |       |
+---+   +---+---+---+   +   +   +---+   +   +---+   +---+---+---+---+   +---+   +   +   +   +---+   +
|                                                                                                   |
+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
```


### Sidewinder

Sidewinder is based upon Binary Tree, but when it navigates south, it chooses a
random cell from the current horizontal corridor and generates the link from
there. The mazes will still flow vertically, but not to the right as with Binary
Tree. All mazes with have a single horizontal corridor along the southern edge.

To generate a maze using the sidewinder algorithm:
```clojure
(require '[meiro.sidewinder :as sw])
(print (ascii/render (sw/create (m/init 8 25))))
```

Which will produce a maze like:
```
+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
|       |           |               |       |   |   |       |       |   |               |   |       |
+   +---+---+   +---+---+---+---+   +---+   +   +   +---+   +   +---+   +   +---+---+---+   +   +---+
|       |   |   |       |   |   |           |   |           |   |       |           |   |   |   |   |
+---+   +   +   +   +---+   +   +---+   +---+   +   +---+---+   +---+   +---+   +---+   +   +   +   +
|   |   |                           |       |       |   |   |       |   |       |           |   |   |
+   +   +---+---+---+   +---+---+---+   +---+   +---+   +   +   +---+   +   +---+---+   +---+   +   +
|       |               |       |                       |   |       |   |   |   |                   |
+---+   +---+---+---+   +   +---+---+   +---+---+---+---+   +---+   +   +   +   +---+   +---+---+---+
|       |           |       |       |   |       |   |   |       |   |       |   |   |       |       |
+---+   +   +---+---+   +---+   +---+   +---+   +   +   +---+   +   +   +---+   +   +---+   +   +---+
|   |                   |   |   |       |           |       |       |   |   |   |       |   |       |
+   +---+---+---+---+   +   +   +---+   +   +---+---+   +---+   +---+   +   +   +---+   +   +---+   +
|                   |   |   |                   |   |   |                   |   |   |       |   |   |
+---+---+---+   +---+   +   +---+---+---+---+   +   +   +---+---+---+   +---+   +   +---+   +   +   +
|                                                                                                   |
+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
```


### Aldous-Broder

To generate a random-walk maze using Aldous-Broder:
```clojure
(require '[meiro.aldous-broder :as ab])
(print (ascii/render (ab/create (m/init 8 25))))
```

Which will produce a maze like:
```
+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
|       |   |           |   |   |               |                   |                               |
+---+   +   +   +---+---+   +   +---+---+   +---+---+   +---+---+---+   +   +   +   +---+---+   +   +
|           |       |           |       |       |               |       |   |   |   |       |   |   |
+   +---+   +   +---+   +---+   +---+   +   +---+---+   +---+   +---+   +   +   +   +---+   +---+   +
|   |           |           |       |   |   |       |       |   |       |   |   |               |   |
+   +---+---+---+---+---+   +---+---+   +   +   +---+---+---+   +---+---+   +   +---+   +   +---+   +
|                       |       |   |                   |   |               |       |   |   |   |   |
+---+---+---+   +   +   +   +   +   +---+   +---+   +   +   +   +---+   +---+   +   +   +---+   +---+
|       |       |   |   |   |       |   |       |   |           |   |   |   |   |   |   |           |
+   +---+   +---+   +---+   +   +---+   +   +---+---+---+   +---+   +   +   +   +---+   +---+   +---+
|               |   |   |   |   |   |   |   |       |           |       |       |   |       |       |
+   +---+   +---+   +   +   +   +   +   +---+---+   +   +   +---+---+   +   +---+   +---+   +   +---+
|   |   |   |               |       |           |   |   |       |       |           |           |   |
+---+   +   +   +---+---+   +   +---+---+   +   +   +   +---+---+---+   +   +---+   +   +   +---+   +
|           |           |   |               |               |           |       |   |   |           |
+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
```


### Wilson's

To generate a loop-erasing, random-walk maze using Wilson's:
```clojure
(require '[meiro.wilson :as w])
(print (ascii/render (w/create (m/init 8 25))))
```

Which will produce a maze like:
```
+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
|                               |       |                   |           |               |           |
+   +   +   +---+   +---+---+   +---+   +   +   +---+   +   +---+---+   +---+   +   +---+   +---+   +
|   |   |       |   |       |           |   |       |   |           |           |   |       |   |   |
+---+---+---+---+   +---+   +---+   +---+   +   +---+---+   +---+---+   +---+---+---+---+---+   +   +
|       |           |           |   |       |       |                       |       |   |   |   |   |
+---+   +   +---+---+   +   +   +---+---+   +---+   +---+---+   +---+   +---+---+   +   +   +   +   +
|       |       |       |   |           |       |       |   |   |   |   |           |   |       |   |
+---+   +   +---+---+---+---+   +---+---+   +---+---+   +   +---+   +   +   +---+---+   +   +---+   +
|       |       |           |   |       |   |               |               |       |               |
+---+   +---+   +---+   +---+   +---+   +---+   +---+---+   +---+---+   +   +   +---+---+   +   +   +
|               |               |               |       |   |   |   |   |           |       |   |   |
+   +   +---+---+   +---+   +---+   +---+---+---+   +---+   +   +   +---+---+   +   +---+   +---+---+
|   |   |               |       |   |               |           |               |                   |
+---+   +---+---+   +   +---+   +   +---+---+   +   +---+   +   +---+---+---+---+   +---+   +---+   +
|                   |   |               |       |           |           |           |       |       |
+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
```


### Hunt and Kill

To generate a random-walk maze biased to first visited cell using Hunt and Kill:
```clojure
(require '[meiro.hunt-and-kill :as hk])
(print (ascii/render (hk/create (m/init 8 25))))
```

Which will produce a maze like:
```
+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
|   |       |                               |       |                                               |
+   +   +   +   +   +---+---+   +---+---+   +   +---+   +---+---+---+   +---+---+   +   +---+---+   +
|       |       |   |           |       |       |       |           |           |   |           |   |
+   +---+---+---+   +---+---+---+   +   +---+   +   +---+   +   +   +---+---+   +   +---+---+   +   +
|           |   |           |       |           |           |   |       |       |   |           |   |
+   +---+   +   +---+---+   +   +---+---+---+---+---+---+---+   +---+   +   +---+   +---+---+---+   +
|       |           |       |               |       |           |       |       |   |               |
+---+---+---+---+   +   +---+---+---+---+   +   +   +---+---+---+   +---+---+   +---+   +---+---+---+
|       |       |   |   |               |   |   |                   |       |       |           |   |
+   +   +   +   +---+   +   +   +---+---+   +   +   +---+---+---+---+   +---+---+   +---+   +   +   +
|   |       |           |   |               |   |       |           |       |       |       |   |   |
+   +---+---+---+---+---+   +---+---+---+---+   +---+---+   +---+   +---+   +   +   +   +---+   +   +
|               |           |               |       |           |       |       |   |   |       |   |
+---+---+---+   +   +---+---+   +   +---+---+---+   +   +---+---+---+---+---+---+---+   +   +   +   +
|               |               |                   |                                   |   |       |
+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
```


### Recursive Backtracker

To generate a random-walk maze biased to last unvisited cell on the path using
the Recursive Backtracker:
```clojure
(require '[meiro.backtracker :as b])
(print (ascii/render (b/create (m/init 8 25))))
```

Which will produce a maze like:
```
+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
|               |                       |       |       |                       |               |   |
+   +---+---+   +   +---+---+---+   +   +   +   +---+   +   +---+   +   +---+---+   +---+   +   +   +
|   |       |   |       |       |   |   |   |       |   |   |   |   |   |       |   |   |   |       |
+   +   +   +   +---+   +---+   +   +   +   +---+   +   +   +   +   +---+   +   +   +   +   +---+   +
|   |   |   |       |   |       |   |   |       |       |       |           |           |   |       |
+   +---+   +---+   +   +   +---+   +   +---+   +---+---+---+   +---+---+---+---+---+---+   +   +---+
|   |           |   |   |       |   |           |               |                   |       |   |   |
+   +   +---+   +   +   +---+   +   +---+---+---+   +---+---+---+---+---+   +   +---+   +---+   +   +
|       |   |   |   |       |   |               |           |               |   |       |           |
+---+---+   +   +   +---+   +   +---+---+---+   +---+---+   +   +---+---+---+   +   +---+---+---+---+
|   |           |   |       |       |       |               |   |               |       |       |   |
+   +   +---+---+   +   +---+   +   +---+   +---+---+---+---+   +---+   +---+---+---+   +   +   +   +
|       |           |           |               |           |       |       |       |       |   |   |
+   +---+   +---+---+---+---+---+---+---+---+   +---+   +   +---+   +---+   +---+   +---+---+   +   +
|       |                                               |           |                       |       |
+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
```


## Solutions

To calculate the distance from the north-east cell to each cell using Dijkstra's
algorithm:
```clojure
(require '[meiro.dijkstra :as d])
(def maze (sw/create (m/init 8 8)))
(def dist (d/distances maze))
(print (ascii/render maze (ascii/show-distance dist)))
```

Which will produce a maze like:
```
+---+---+---+---+---+---+---+---+
| 0   1 | 4 | n | q   p | o   n |
+   +---+   +   +---+   +---+   +
| 1   2   3 | m   l | o   n | m |
+   +---+---+---+   +---+   +   +
| 2   3 | m   l | k   l | m | l |
+---+   +---+   +   +---+   +   +
| 5   4 | l   k   j   k | l | k |
+   +---+---+---+   +---+   +   +
| 6 | 9 | k   j   i | h | k   j |
+   +   +---+---+   +   +---+   +
| 7   8   9   a | h   g | j | i |
+---+---+   +---+---+   +   +   +
| g   f | a   b | g   f | i   h |
+---+   +---+   +---+   +---+   +
| f   e   d   c   d   e   f   g |
+---+---+---+---+---+---+---+---+
```

To calculate and show a solution:
```clojure
(def maze (b/create (m/init 8 25)))
(def sol (d/solution maze [0 0] [0 24]))
(print (ascii/render maze (ascii/show-solution sol)))
```

Which will produce a maze like:
```
+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
| *     |           | *   * | *   *                 | *   *   *   *   *   * |         *   * | *   * |
+   +   +   +   +---+   +   +   +   +---+---+---+   +   +---+   +---+---+   +---+---+   +   +   +   +
| * |       |   | *   * | *   * | *   * |           | * |   |   |       | * | *   * | * | *   * |   |
+   +---+---+---+   +---+---+---+---+   +---+---+---+   +   +   +   +   +   +   +   +   +---+---+   +
| *   * | *   * | * | *   * |       | * | *   *   *   * |   |   |   |   | *   * | *   * |   |       |
+---+   +   +   +   +   +   +   +   +   +   +---+---+---+   +   +   +   +---+---+---+---+   +   +---+
| *   * | * | * | *   * | *   * |   | * | * |               |       |   |               |   |       |
+   +---+   +   +---+---+---+   +---+   +   +---+---+   +   +---+---+   +   +---+---+   +   +---+   +
| *   *   * | *     | *   *   * | *   * | * | *   * |   |           |       |   |       |       |   |
+---+---+---+   +   +   +---+---+   +   +   +   +   +---+---+   +   +---+---+   +   +---+---+   +   +
| *   *   *   * |   | *   *     | * |   | * | * | *   *   * |   |               |           |       |
+   +---+---+   +---+---+   +---+   +   +   +   +---+---+   +---+---+---+---+   +---+---+   +   +---+
| *   *   * |   | *   * | * | *   * |   | *   * | *   * | *   *   *   *   *   *   * |   |   |   |   |
+   +---+   +---+   +   +   +   +---+---+---+---+   +   +---+---+---+---+---+---+   +   +   +   +   +
|       | *   *   * | *   * | *   *   *   *   *   * | *   *   *   *   *   *   *   * |       |       |
+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
```


## License

Copyright © 2017 Michael S. Daines

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
