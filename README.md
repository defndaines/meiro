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


## License

Copyright © 2017 Michael S. Daines

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
