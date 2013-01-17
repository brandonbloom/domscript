(ns bbloom.domscript.core
  (:refer-clojure :exclude [remove])
  (:require [clojure.string :as str]
            [bbloom.domscript.svg :as svg]))

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

(defn parent [element]
  (.getParentNode element))

(defn children [element]
  (let [nodes (.getChildNodes element)
        length (.getLength nodes)]
    (loop [i 0 children []]
      (if (< i length)
        (recur (inc i) (conj children (.item nodes i)))
        children))))


;;;; Attributes

(defn attribute [element attribute]
  (.getAttribute element (name attribute)))

(defn set-attribute [element attribute value]
  (.setAttribute element (name attribute) (str value)))

(defn set-attributes [element attributes]
  (doseq [[attribute value] attributes]
    (set-attribute element attribute value)))

(defn classes [element]
  (->> (str/split (attribute element :class) #" ")
    (filter seq)
    set))

(defn set-classes [element classes]
  (set-attribute element :class (str/join " " classes)))

(defn update-classes [element f & args]
  (set-classes element (apply f (classes element) args)))

(defn add-class [element class]
  (update-classes element conj class))

(defn add-classes [element classes]
  (update-classes element #(apply conj % classes)))

(defn remove-class [element class]
  (update-classes element disj class))

(defn remove-classes [element classes]
  (update-classes element #(apply disj % classes)))

(defn has-class? [element class]
  (contains? (classes element) class))


;;;; Manipulation

(defn create-element [tag]
  (if (string? tag)
    (.createElement *document* tag)
    (.createElementNS *document* (namespaces (namespace tag)) (name tag))))

(defn append [parent elements]
  (each-element elements #(.appendChild parent %)))

(defn remove [elements]
  (each-element elements #(.removeChild (parent %) %)))
