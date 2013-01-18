(ns bbloom.domscript.svg
  (:import [org.apache.batik.dom.svg SVGDOMImplementation]
           [java.awt Dimension]
           [javax.swing JFrame]
           [org.apache.batik.swing JSVGCanvas]))

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
       :document document})))

(defn update-document! [window f & args]
  (.. (:canvas window) getUpdateManager getUpdateRunnableQueue
    (invokeLater #(apply f (:document window) args))))
