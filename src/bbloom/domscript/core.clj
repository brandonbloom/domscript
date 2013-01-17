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

(defn toggle-member [set x]
  (if (contains? set x)
    (disj set x)
    (conj set x)))


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

(defn set-attribute [elements attribute value]
  (each-element elements #(.setAttribute % (name attribute) (str value))))

(defn set-attributes [elements attributes]
  (each-element elements
    #(doseq [[attribute value] attributes]
       (set-attribute % attribute value))))

(defn remove-attribute [elements attribute]
  (each-element elements #(.removeAttribute % (name attribute))))

(defn remove-attributes [elements attributes]
  (each-element elements
    #(doseq [attribute attributes]
       (.removeAttribute % (name attribute)))))

(defn classes [element]
  (->> (str/split (attribute element :class) #" ")
    (filter seq)
    set))

(defn set-classes [elements classes]
  (set-attribute elements :class (str/join " " classes)))

(defn update-classes [elements f & args]
  (each-element elements
    #(set-classes elements (apply f (classes %) args))))

(defn add-class [elements class]
  (update-classes elements conj class))

(defn add-classes [elements classes]
  (update-classes elements #(apply conj % classes)))

(defn remove-class [elements class]
  (update-classes elements disj class))

(defn remove-classes [elements classes]
  (update-classes elements #(apply disj % classes)))

(defn has-class? [element class]
  (contains? (classes element) class))

(defn toggle-class [elements class]
  (update-classes elements #(toggle-member % class)))

(defn toggle-classes [elements classes]
  (update-classes elements #(reduce toggle-member % classes)))


;;;; Manipulation

(defn create-element [tag]
  (if (string? tag)
    (.createElement *document* tag)
    (.createElementNS *document* (namespaces (namespace tag)) (name tag))))

(defn append [parent elements]
  (each-element elements #(.appendChild parent %)))

(defn remove [elements]
  (each-element elements #(.removeChild (parent %) %)))
