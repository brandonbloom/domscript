(ns bbloom.domscript.cat
  (:refer-clojure :exclude [remove])
  (:require [factjor.core :as cat
             :refer (defprim defword defvoid0 defvoid1 defop0 defop1 defop2)]
            [bbloom.domscript.core :as dom]))

;;;; Traversal

(defop0 document-element dom/document-element)

(defop1 element-with-id dom/element-with-id)
(defop1 elements-with-tag dom/elements-with-tag)

(defop1 parent dom/parent)

(defop1 children dom/children)


;;;; Attributes

(defprim attribute [element attribute -- element]
  (conj $ element (dom/attribute element attribute)))

(defprim set-attribute [elements attribute value -- elements]
  (dom/set-attribute elements attribute value)
  (conj $ elements))

(defprim set-attributes [elements attributes -- elements]
  (dom/set-attributes elements attributes)
  (conj $ elements))

(defprim remove-attribute [elements attribute -- elements]
  (dom/remove-attribute elements attribute)
  (conj $ elements))

(defprim remove-attributes [elements attributes -- elements]
  (dom/remove-attributes elements attributes)
  (conj $ elements))


;;;; CSS

;;; Classes

(defprim classes [element -- element classes]
  (conj $ element (dom/classes element)))

(defprim has-class? [element class -- element bool]
  (conj $ element (dom/has-class? element class)))

(defprim set-classes [elements classes -- elements]
  (dom/set-classes elements classes)
  (conj $ elements))

(defprim add-class [elements class -- elements]
  (dom/add-class elements class)
  (conj $ elements))

(defprim add-classes [elements classes -- elements]
  (dom/add-classes elements classes)
  (conj $ elements))

(defprim remove-class [elements class -- elements]
  (dom/remove-class elements class)
  (conj $ elements))

(defprim remove-classes [elements classes -- elements]
  (dom/remove-classes elements classes)
  (conj $ elements))

(defprim toggle-class [elements class -- elements]
  (dom/toggle-class elements class)
  (conj $ elements))

(defprim toggle-classes [elements classes -- elements]
  (dom/toggle-classes elements classes)
  (conj $ elements))

;;; Styles

(defprim style [element property -- element value]
  (conj $ element (dom/style element property)))

(defprim set-style [elements property value -- elements]
  (conj $ elements (dom/set-style elements property value)))

(defprim set-styles [elements styles -- elements]
  (conj $ elements (dom/set-styles elements styles)))


;;;; Data

(defprim all-data [element -- element data]
  (conj $ element (dom/all-data element)))

(defprim get-data [element key]
  (conj $ element (dom/get-data element key)))

(defprim add-data [elements key value -- elements]
  (dom/add-data elements key value)
  (conj $ elements))

(defprim remove-data [elements key -- elements]
  (dom/remove-data elements key)
  (conj $ elements))


;;;; Manipulation

(defop1 create-element dom/create-element)

(defprim append [parent elements -- parent]
  (dom/append parent elements)
  (conj $ parent))

(defvoid1 remove dom/remove)
