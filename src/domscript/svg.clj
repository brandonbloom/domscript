(ns domscript.svg
  (:refer-clojure :exclude [send])
  (:import [org.apache.batik.dom.svg SVGDOMImplementation]
           [java.awt Dimension]
           [javax.swing JFrame]
           [org.w3c.dom.traversal NodeFilter]
           [org.apache.batik.swing JSVGCanvas]
           [org.apache.batik.css.parser Parser]
           [org.apache.batik.css.engine.sac CSSSelectorFactory
                                            CSSConditionFactory]
           [org.apache.batik.dom.traversal TraversalSupport]))

(def dom-impl (SVGDOMImplementation/getDOMImplementation))
(def ns-uri SVGDOMImplementation/SVG_NAMESPACE_URI)

(defn create-document []
  (.createDocument dom-impl ns-uri "svg" nil))

(defn create-window
  ([] (create-window {}))
  ([{:keys [title width height] :as options
     :or {title "DomScript-SVG"
          width 640 height 480}}]
    (let [frame (JFrame. title)
          document (create-document)
          canvas (JSVGCanvas.)]
      (doto (.getDocumentElement document)
        (.setAttribute "title" title)
        (.setAttribute "width" (str width))
        (.setAttribute "height" (str height)))
      (doto canvas
        (.setPreferredSize (Dimension. width height))
        (.setDocumentState JSVGCanvas/ALWAYS_DYNAMIC)
        (.setSVGDocument document))
      (doto frame
        (.. getContentPane (add canvas))
        (.pack)
        (.setVisible true))
      {:canvas canvas
       :frame frame
       :document document
       :handlers (atom nil)})))

(defn send [window f & args]
  (.. (:canvas window) getUpdateManager getUpdateRunnableQueue
    (invokeLater #(apply f args))))

(def ^:private condition-factory
  (CSSConditionFactory. nil "class" nil "id"))

(defn- parse-selector [selector]
  (let [parser (Parser.)]
    (doto parser
      (.setSelectorFactory CSSSelectorFactory/INSTANCE)
      (.setConditionFactory condition-factory))
    (.parseSelectors parser selector)))

(defn- matches?
  ([selector element] (matches? selector element ""))
  ([selector element pseudo]
   (let [length (.getLength selector)]
     (loop [i 0]
       (if (< i length)
         (if (.. selector (item i) (match element pseudo))
           true
           (recur (inc i)))
         false)))))

(defn selection-seq [root selector]
  (let [selector (parse-selector selector)
        iterator (.createNodeIterator (TraversalSupport.)
                   (.getOwnerDocument root)
                   root
                   NodeFilter/SHOW_ELEMENT
                   (reify NodeFilter
                     (acceptNode [_ element]
                       (if (matches? selector element)
                         NodeFilter/FILTER_ACCEPT
                         NodeFilter/FILTER_REJECT)))
                   false)
        node-seq ((fn step []
                    (lazy-seq
                      (when-let [node (.nextNode iterator)]
                        (cons node (step))))))]
    ;; Iterator always returns the reference node, so match it.
    (when-let [node (first node-seq)]
      (if (matches? selector (first node-seq))
        node-seq
        (next node-seq)))))
