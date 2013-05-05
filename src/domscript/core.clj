(ns domscript.core
  (:refer-clojure :exclude [remove])
  (:require [clojure.string :as str]
            [domscript.svg :as svg])
  (:import [org.w3c.dom.events EventListener]))


;;;; Kernel

(def ^:dynamic *window*)

(def namespaces
  {"svg" svg/ns-uri})


;;;; Utilities

(defn collify [x]
  (if (or (nil? x) (coll? x)) x [x]))

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
  (.getDocumentElement (:document *window*)))

(defn element-with-id [id]
  (.getElementById (:document *window*) id))

(defn elements-with-tag [tag]
  (let [[ns name] (split-name tag)]
    ;TODO ->seq ?
    (NodeList->vector (.getElementsByTagNameNS (:document *window*) ns name))))

(defn select
  ([selector] (select (document-element) selector))
  ([root selector]
    (svg/selection-seq root selector)))

(defn subselect [root selector] ;NOTE exists for ease of cat-ops
  (select root selector))

(defn parent [element]
  (.getParentNode element))  ;;TODO return a coll if arg is coll

(defn children [element]
  (NodeList->vector (.getChildNodes element))) ;;TODO mapcat element(s)

(defn element-seq [root]
  (subselect root "*"))

(defn descendents [element]
  (next (element-seq element)))


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

;; Listeners are indexed by key and by element in (:handlers *window*)
;;   [:elements element event-ns event-name listener] => key
;;   [:keys key event-ns event-name listener] => #{elements}

(defn bind
  ([elements event-type callback]
    (bind elements event-type callback callback))
  ([elements event-type key callback]
    (let [elements (set (collify elements))
          [ns name] (split-name event-type)
          listener (reify EventListener
                     (handleEvent [_ evt]
                       (callback evt)))]
      (update-in *window* [:handlers] swap!
        (fn [handlers]
          (reduce
            (fn [handlers element]
              (assoc-in handlers [:elements element ns name listener] key))
            (assoc-in handlers [:keys key ns name listener] elements)
            elements)))
      (each-element elements
        #(.addEventListenerNS % ns name listener false nil)))))

(defn- prune-empty [m ks]
  (if (and (next ks) (empty? (get-in m ks)))
    (let [ks* (pop ks)]
      (recur (update-in m ks* dissoc (peek ks)) ks*))
    m))

(defn- clean-listeners [args]
  (update-in *window* [:handlers] swap!
    (fn [handlers]
      (reduce
        (fn [handlers [element key ns name listener :as x]]
          (-> handlers
            ;; Bulk removal would be more efficient, but there's a deref race
            (update-in [:keys key ns name listener] disj element)
            (prune-empty [:keys key ns name listener])
            (update-in [:elements element ns name] dissoc listener)
            (prune-empty [:elements element ns name listener])))
        handlers
        args))))

(defn unbind [key]
  (let [args (for [[ns names] (get-in @(:handlers *window*) [:keys key])
                   [name listeners] names
                   [listener elements] listeners
                   element elements]
                [element key ns name listener])]
    (clean-listeners args)
    (doseq [[element key ns name listener] args]
      (.removeEventListenerNS element ns name listener false))))

(defn- clean [elements]
  (let [handlers @(:handlers *window*)
        args (for [element (collify elements)
                   [ns names] (get-in handlers [:elements element])
                   [name listeners] names
                   [listener key] listeners]
               [element key ns name listener])]
    (clean-listeners args)))


;;;; Manipulation

(defn create-element [tag]
  (let [[ns name] (split-name tag)]
    (.createElementNS (:document *window*) ns name)))

(defn append [parent elements]
  (each-element elements #(.appendChild parent %)))

(defn remove [elements]
  (doseq [element (collify elements)]
    (doseq [descendent (descendents element)]
      (clean descendent))
    (clean element)
    (.removeChild (parent element) element)))
