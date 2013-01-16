(ns bbloom.domscript.core
  (:refer-clojure :exclude [remove])
  (:require [bbloom.domscript.svg :as svg]))

;;;; Utilities

(defn collify [x]
  (if (coll? x) x [x]))

(defn each-element [elements f]
  (doseq [element (collify elements)]
    (f element)))

;;;; Kernel

(def ^:dynamic *document*)

(def namespaces
  {"svg" svg/ns-uri})

;;;; Traversal

(defn document-element []
  (.getDocumentElement *document*))

(defn children [element]
  (let [nodes (.getChildNodes element)
        length (.getLength nodes)]
    (loop [i 0 children []]
      (if (< i length)
        (recur (inc i) (conj children (.item nodes i)))
        children))))

;;;; Attributes

(defn set-attribute [element attribute value]
  (.setAttribute element (name attribute) (str value)))

(defn set-attributes [element attributes]
  (doseq [[attribute value] attributes]
    (set-attribute element attribute value)))

;;;; Manipulation

(defn create-element [tag]
  (if (string? tag)
    (.createElement *document* tag)
    (.createElementNS *document* (namespaces (namespace tag)) (name tag))))

(defn append [parent elements]
  (each-element elements #(.appendChild parent %)))

(defn remove [elements]
  (each-element elements #(.removeChild (.getParent %) %)))
