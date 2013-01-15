(ns bbloom.domscript.svg
  (:import [org.apache.batik.dom.svg SVGDOMImplementation]
           [javax.swing JFrame SwingUtilities]
           [org.apache.batik.swing JSVGCanvas]))

(def dom-impl (SVGDOMImplementation/getDOMImplementation))
(def ns-uri SVGDOMImplementation/SVG_NAMESPACE_URI)

(defn create-document []
  (.createDocument dom-impl ns-uri "svg" nil))

(defn test-frame []
  (let [frame (JFrame. "DomScript-SVG")
        width 640, height 480
        doc (create-document)
        canvas (JSVGCanvas.)]
    (doto (.getDocumentElement doc)
      (.setAttribute "width" (str width))
      (.setAttribute "height" (str height)))
    (doto canvas
      (.setDocumentState JSVGCanvas/ALWAYS_DYNAMIC)
      (.setSVGDocument doc))
    (doto frame
      (.setSize width height)
      (.. getContentPane (add canvas))
      (.setVisible true))
    (def canvas canvas)
    (def frame frame)
    (def doc doc)
    ))

(defn update-document! [f & args]
  (.. canvas getUpdateManager getUpdateRunnableQueue
    (invokeLater #(apply f doc args))))

(comment

  (test-frame)

  (update-document! identity)

)
