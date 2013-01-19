(ns bbloom.domscript.core
  (:refer-clojure :exclude [remove])
  (:require [clojure.string :as str]
            [bbloom.domscript.svg :as svg])
  (:import [org.w3c.dom.events EventListener]))


;;;; Kernel

(def ^:dynamic *document*)

(def namespaces
  {"svg" svg/ns-uri})


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

(defn NodeList->vector [nodes]
  (let [length (.getLength nodes)]
    (loop [i 0 v []]
      (if (< i length)
        (recur (inc i) (conj v (.item nodes i)))
        v))))

(defn split-name [x]
  (if (string? x)
    [nil x]
    [(namespaces (namespace x)) (name x)]))


;;;; Traversal

(defn document-element []
  (.getDocumentElement *document*))

(defn element-with-id [id]
  (.getElementById *document* id))

(defn elements-with-tag [tag]
  (let [[ns name] (split-name tag)]
    (NodeList->vector (.getElementsByTagNameNS *document* ns name))))

(defn select
  ([selector] (select (document-element) selector))
  ([root selector]
    (vec (svg/selection-seq root selector))))

(defn subselect [root selector] ;NOTE exists for ease of cat-ops
  (select root selector))

(defn parent [element]
  (.getParentNode element))  ;;TODO return a coll if arg is coll

(defn children [element]
  (NodeList->vector (.getChildNodes element))) ;;TODO mapcat


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


;;;; CSS

;;; Classes

(defn classes [element]
  (->> (str/split (attribute element :class) #" ")
    (filter seq)
    set))

(defn has-class? [element class]
  (contains? (classes element) class))

(defn set-classes [elements classes]
  (set-attribute elements :class (str/join " " classes)))

(defn update-classes [elements f & args]
  (each-element elements
    #(set-classes % (apply f (classes %) args))))

(defn add-class [elements class]
  (update-classes elements conj class))

(defn add-classes [elements classes]
  (update-classes elements #(apply conj % classes)))

(defn remove-class [elements class]
  (update-classes elements disj class))

(defn remove-classes [elements classes]
  (update-classes elements #(apply disj % classes)))

(defn toggle-class [elements class]
  (update-classes elements #(toggle-member % class)))

(defn toggle-classes [elements classes]
  (update-classes elements #(reduce toggle-member % classes)))

;;; Styles

(defn style [element property]
  (.. element getStyle (getPropertyValue (name property))))

(defn set-style [elements property value]
  (each-element elements
    #(.. % getStyle (setProperty (name property) value ""))))

(defn set-styles [elements styles]
  (each-element elements
    #(doseq [[property value] styles]
       (.. % getStyle (setProperty (name property) value "")))))


;;;; Data

(def ^:private data-key "DomScript_data")

(defn all-data [element]
  (or (.getUserData element data-key) {}))

(defn reset-data [elements data]
  (each-element elements
    #(.setUserData % data-key data nil)))

(defn get-data [element key]
  (get (all-data element) key))

(defn swap-data [elements f & args]
  (each-element elements
    #(reset-data % (apply f (all-data %) args))))

(defn add-data [elements key value]
  (swap-data elements assoc-in [key] value))

(defn remove-data [elements key]
  (swap-data elements dissoc key))


;;;; Dimensions


;;;; Effects
; Should I bother with these? Do these belong in another library?


;;;; Events

(def ^:private handlers (atom {}))

(def ^:private conjs (fnil conj #{}))

(defn bind [elements event-type key callback]
  (let [[ns name] (split-name event-type)
        listener (reify EventListener
                   (handleEvent [_ evt]
                     (callback evt)))]
    (swap! handlers update-in [key [ns name]] conjs [listener elements])
    (each-element elements
      #(.addEventListenerNS % ns name listener false nil))))

(defn unbind [key]
  (doseq [[[ns name] groups] (@handlers key)
          [listener elements] groups]
    (each-element elements
      #(.removeEventListenerNS % ns name listener false))))


;;;; Manipulation

(defn create-element [tag]
  (let [[ns name] (split-name tag)]
    (.createElementNS *document* ns name)))

(defn append [parent elements]
  (each-element elements #(.appendChild parent %)))

(defn remove [elements]
  (each-element elements #(.removeChild (parent %) %)))
