(ns bbloom.domscript.cat
  (:refer-clojure :exclude [remove])
  (:require [factjor.core :as cat
             :refer (defprim defword defvoid0 defvoid1 defop0 defop1 defop2)]
            [bbloom.domscript.core :as dom]))

;;;; Traversal

(defop0 document-element dom/document-element)

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

(defprim classes [element -- classes]
  (conj $ element (dom/classes element)))

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

(defprim has-class? [element class -- bool]
  (conj $ element (dom/has-class? element class)))

(defprim toggle-class [elements class -- elements]
  (dom/toggle-class elements class)
  (conj $ elements))

(defprim toggle-classes [elements classes -- elements]
  (dom/toggle-classes elements classes)
  (conj $ elements))



;;;; Manipulation

(defop1 create-element dom/create-element)

(defprim append [parent elements -- parent]
  (dom/append parent elements)
  (conj $ parent))

(defvoid1 remove dom/remove)
