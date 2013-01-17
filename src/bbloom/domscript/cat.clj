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

(defprim set-attribute [element attribute value -- element]
  (dom/set-attribute element attribute value)
  (conj $ element))

(defprim set-attributes [element attributes -- element]
  (dom/set-attributes element attributes)
  (conj $ element))

(defprim classes [element -- classes]
  (conj $ element (dom/classes element)))

(defprim add-class [element class -- element]
  (dom/add-class element class)
  (conj $ element))

(defprim add-classes [element classes -- element]
  (dom/add-classes element classes)
  (conj $ element))

(defprim remove-class [element class -- element]
  (dom/remove-class element class)
  (conj $ element))

(defprim remove-classes [element classes -- element]
  (dom/remove-classes element classes)
  (conj $ element))

(defprim has-class? [element class -- bool]
  (conj $ element (dom/has-class? element class)))


;;;; Manipulation

(defop1 create-element dom/create-element)

(defprim append [parent elements -- parent]
  (dom/append parent elements)
  (conj $ parent))

(defvoid1 remove dom/remove)
