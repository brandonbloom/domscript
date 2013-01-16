(ns bbloom.domscript
  (:require [factjor.core :as cat :refer (defprim defword)]
            [bbloom.domscript.svg :as svg]))

(def namespaces
  {"svg" svg/ns-uri})

(def ^:dynamic *document*)

(defprim create-element [tag -- element]
  (conj $
    (if (string? tag)
      (.createElement *document* tag)
      (.createElementNS *document* (namespaces (namespace tag)) (name tag)))))

(defprim document-element [-- element]
  (conj $ (.getDocumentElement *document*)))

(defn- set-attribute* [element attribute value]
  (.setAttribute element (name attribute) (str value))
  nil)

(defprim set-attribute [element attribute value -- element]
  (set-attribute* element attribute value)
  (conj $ element))

(defprim set-attributes [element attributes -- element]
  (doseq [[attribute value] attributes]
    (set-attribute* element attribute value))
  (conj $ element))

(defprim append-child [parent child]
  (.appendChild parent child)
  (conj $ parent))

(comment

  (svg/update-document!
    (fn [document]
      (binding [*document* document]
        (cat/run
          document-element
          (create-element :svg/rect)
          (set-attributes {:x 70 :y 50
                           :width 10 :height 30
                           :fill "red"})
          append-child)
        )))

)
