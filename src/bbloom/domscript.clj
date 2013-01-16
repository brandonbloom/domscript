(ns bbloom.domscript
  (:require [factjor.core :as cat :refer (defprim defword)]
            [bbloom.domscript.svg :as svg]))

(def namespaces
  {"svg" svg/ns-uri})

(def ^:dynamic *document*)

;;; Many of these primitives could probably be regular words if not for perf.

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

(defprim append-child [parent child -- parent]
  (.appendChild parent child)
  (conj $ parent))

(defprim append-children [parent children -- parent]
  (doseq [child children]
    (.appendChild parent child))
  (conj $ parent))

(defn- children* [element]
  (let [nodes (.getChildNodes element)
        length (.getLength nodes)]
    (loop [i 0 children []]
      (if (< i length)
        (recur (inc i) (conj children (.item nodes i)))
        children))))

(defprim children [element -- children]
  (conj $ (children* element)))

(defprim remove-child [parent child -- parent]
  (.removeChild parent child)
  (conj $ parent))

(defprim remove-children [element -- element]
  (doseq [child (children* element)]
    (.removeChild element child))
  (conj $ element))

(comment

  (svg/update-document!
    (fn [document]
      (binding [*document* document]
        (cat/run

          document-element
          remove-children

          (create-element :svg/rect)
          (set-attributes {:x 70 :y 50
                           :width 10 :height 30
                           :fill "red"})
          append-child
          )

        )))

)
