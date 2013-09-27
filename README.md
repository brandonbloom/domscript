# What is DomScript?

DomScript aims to be to browsers what PostScript is to printers.

It's a DOM manipulation library for Clojure in the concatenative paradigm.

### What does this have to do with Printers?

See Video: [Concatenative Clojure at Clojure/WEST 2013][1].

# Status

This preliminary release of DomScript is implemented in Clojure, not
ClojureScript. A future version of DomScript will include a ClojureScript port.
In the meantime, Apache Batik provides a JVM-side DOM implementation and SVG
renderer. Batik's DOM API is nearly identical to that of a browser's without
the added complexity of development in a stratified execution environment.
As awesome as ClojureScript is, it slows iteration times just enough to
motivate JVM-first development. Priority #1 is to design the concatenative API.

# Usage

```clojure
(require '[factjor.core :as cat])
(require '[domscript.svg :as svg])
(require '[domscript.cat :as dom])

(def window (svg/create-window))

(dom/run window
  dom/document-element
  (dom/create-element :svg/rect)
  (dom/set-attributes {:x 100 :y 50
                       :width 200
                       :height 75
                       :fill "red"})
  dom/append
)
```

See [cat.clj][2] for a complete listing of [Factjor][3] words.

# License

Copyright © 2013 Brandon Bloom

Distributed under the Eclipse Public License, the same as Clojure.

[1]: http://www.infoq.com/presentations/concatenative-clojure
[2]: ./src/domscript/cat.clj
[3]: https://github.com/brandonbloom/factjor
