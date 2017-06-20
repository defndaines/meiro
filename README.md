# Meiro 迷路

Maze generation code, inspired by working through [Mazes for
Programmers](https://pragprog.com/book/jbmaze/mazes-for-programmers).
Because the book leans on Object Oriented design (coded in Ruby), much of this
is a re-thinking of the approaches in a Clojure style.

Each maze generation algorithm is in its own namespace.

| [Usage](#usage)
| [Algorithms](#algorithms)
| [Solutions](#solutions)
| [Utilities](#utilities)


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

![Sample Maze](img/sample-maze.png)

To print a maze with masked cells:
```clojure
(def grid (ascii/read-grid "template.txt"))
(png/render-masked (b/create grid))
```

![Masked Maze](img/masked-maze.png)

To print a circular (polar) maze:
```clojure
(png/render-polar
  (b/create (polar/init 10) [0 0] polar/neighbors polar/direction))
```

![Polar Maze](img/polar-maze.png)

To print a sigma (hex) maze:
```clojure
(png/render-hex
  (b/create (m/init 15 20) [7 9] hex/neighbors hex/direction))
```

![Sigma Maze](img/sigma-maze.png)

To print a delta (triangle) maze:
```clojure
(def grid (ascii/read-grid "test/meiro/triangle.txt"))
(png/render-delta
  (b/create grid [0 12] triangle/neighbors m/direction))
```

![Delta Maze](img/delta-maze.png)


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
(png/render (bt/create (m/init 8 25)))
```

Which will produce a maze like:

![Binary Tree Maze](img/binary-tree-maze.png)


### Sidewinder

Sidewinder is based upon Binary Tree, but when it navigates south, it chooses a
random cell from the current horizontal corridor and generates the link from
there. The mazes will still flow vertically, but not to the right as with Binary
Tree. All mazes with have a single horizontal corridor along the southern edge.

To generate a maze using the sidewinder algorithm:
```clojure
(require '[meiro.sidewinder :as sw])
(png/render (sw/create (m/init 8 25)))
```

Which will produce a maze like:

![Sidewinder Maze](img/sidewinder-maze.png)


### Aldous-Broder

To generate a random-walk maze using Aldous-Broder:
```clojure
(require '[meiro.aldous-broder :as ab])
(png/render (ab/create (m/init 8 25)))
```

Which will produce a maze like:

![Aldous-Broder Maze](img/aldous-broder-maze.png)


### Wilson's

To generate a loop-erasing, random-walk maze using Wilson's:
```clojure
(require '[meiro.wilson :as w])
(png/render (w/create (m/init 8 25)))
```

Which will produce a maze like:

![Wilson's Maze](img/wilsons-maze.png)


### Hunt and Kill

To generate a random-walk maze biased to first visited cell using Hunt and Kill:
```clojure
(require '[meiro.hunt-and-kill :as hk])
(png/render (hk/create (m/init 8 25)))
```

Which will produce a maze like:

![Hunt and Kill Maze](img/hunt-and-kill-maze.png)


### Recursive Backtracker

To generate a random-walk maze biased to last unvisited cell on the path using
the Recursive Backtracker:
```clojure
(require '[meiro.backtracker :as b])
(png/render (b/create (m/init 8 25)))
```

Which will produce a maze like:

![Recursive Backtracker Maze](img/backtracker-maze.png)


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


## Utilities

There are a few additional utilities besides deriving solutions.


### Longest Path

TBD


### Braid

By default, the algorithms produce "perfect" mazes, i.e., every position in the
grid has one path to any other position in the grid. This inevitably produces
dead ends. "Braiding" is the act of removing dead ends from a maze by linking
them with neighbors.

To enumerate the dead ends in a maze:
```clojure
(def maze (b/create (m/init 8 22)))
(m/dead-ends maze)

([0 10] [0 16] [1 1] [1 21] [2 5] [2 13] [3 0] [3 7] [4 2] [4 13] [4 15] [5 3]
 [5 10] [6 1] [6 15] [6 19] [7 11] [7 21])
```

You can remove all dead ends with the `braid` function.
```clojure
(m/braid maze)
```

If you don't want to remove all dead ends, you can pass in a rate which will determine what percentage of the dead ends should be removed (randomly).
```clojure
(def braided (m/braid maze 0.4))
(png/render braided)
```

![Braided Maze](img/braided-maze.png)


## License

Copyright © 2017 Michael S. Daines

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
