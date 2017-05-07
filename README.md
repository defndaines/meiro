# Meiro 迷路

Maze generation code, inspired by working through [Mazes for
Programmers](https://pragprog.com/book/jbmaze/mazes-for-programmers).
Because the book leans on Object Oriented design (coded in Ruby), much of this
is a re-thinking of the approaches in a Clojure style.

Each maze generation algorithm is in its own namespace.


## Usage

Project is currently _under development_ and does not have functions exposed for
external use.

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

Continuing from there, if you wish to generate a maze using the sidewinder
algorithm:
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

And to render a maze as a PNG:
```clojure
(require '[meiro.png :as png])
(png/render (sw/create (m/init 15 20)) "sample-maze.png")
```
Which creates a PNG file like:
![Sample Maze](sample-maze.png)

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
(def maze (sw/create (m/init 8 25)))
(def sol (d/solution maze [0 0] [0 24]))
(print (ascii/render maze (ascii/show-solution sol)))
```

Which will produce a maze like:
```
+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
| *   *   * |       |           |   |   |           |   |   |       |   |   |   |   |       |   | * |
+---+---+   +   +---+---+---+   +   +   +---+   +---+   +   +   +---+   +   +   +   +---+   +   +   +
|   |     * |   |                   |   |   |   |               |       |   |   |         *   *   * |
+   +---+   +   +---+---+   +---+---+   +   +   +   +---+---+---+   +---+   +   +---+---+   +---+---+
|       | *   *                 |   |       |   |   |                           |   |   | * |   |   |
+---+   +---+   +---+---+---+---+   +---+   +   +   +---+---+---+---+   +---+---+   +   +   +   +   +
|           | * |                   |           |       |               |   |   |       | *   *   * |
+---+   +---+   +---+   +---+---+---+   +---+---+---+   +---+   +---+---+   +   +---+   +---+---+   +
|   |   |   | *   * |                   |       |   |           |                           |   | * |
+   +   +   +---+   +---+---+   +---+---+---+   +   +   +---+---+---+---+   +---+---+---+---+   +   +
|   |       |   | * |       |       |   |           |   |           |       |   |   |       | *   * |
+   +---+   +   +   +   +---+---+   +   +---+---+   +   +   +---+---+---+   +   +   +---+   +   +---+
|           |     * |           |   |           |       |   |               |               | *   * |
+   +---+---+---+   +   +---+---+   +   +---+---+   +---+   +   +---+---+---+   +---+---+---+---+   +
|                 *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   *   * |
+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
```


## License

Copyright © 2017 Michael S. Daines

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
