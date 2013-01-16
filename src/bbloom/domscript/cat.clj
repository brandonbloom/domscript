(ns bbloom.domscript.cat
  (:refer-clojure :exclude [remove])
  (:require [factjor.core :as cat
             :refer (defprim defword defvoid0 defvoid1 defop0 defop1 defop2)]
            [bbloom.domscript.core :as dom]))

;;;; Traversal

(defop0 document-element dom/document-element)

(defop1 children dom/children)

;;;; Attributes

(defprim set-attribute [element attribute value -- element]
  (dom/set-attribute element attribute value)
  (conj $ element))

(defprim set-attributes [element attributes -- element]
  (dom/set-attributes element attributes)
  (conj $ element))

;;;; Manipulation

(defop1 create-element dom/create-element)

(defprim append [parent elements -- parent]
  (dom/append parent elements)
  (conj $ parent))

(defvoid1 remove dom/remove)
