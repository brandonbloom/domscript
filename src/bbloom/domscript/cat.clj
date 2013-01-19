(ns bbloom.domscript.cat
  (:refer-clojure :exclude [remove])
  (:require [factjor.core :as cat :refer (defprim)]
            [bbloom.domscript.core :as dom]))

;;; Helper macros that belong in Factjor

(defn parse-stack-effect [effect]
  (let [[inputs [sep & outputs]] (split-with (complement '#{--}) effect)]
    (assert (= sep '--) "Expected '-- in stack effect")
    {:inputs (vec inputs) :outputs (vec outputs)}))

(defn cat-op-form [f effect]
  (let [{:keys [inputs outputs]} (parse-stack-effect effect)
        input-set (set inputs)
        return-values (clojure.core/remove input-set outputs)
        result-sym (gensym)
        output-map {(first return-values) result-sym}
        conj-syms (map #(get output-map % %) outputs)]
    (assert (<= (count return-values) 1)
            (str "Unable to infer " f " output stack from: " effect))
  `(defprim ~(-> f name symbol) ~inputs
     (let [~result-sym (~f ~@inputs)]
       ~(if (empty? conj-syms)
          '$
          `(conj ~'$ ~@conj-syms))))))

(defmacro cat-ops [ns & op-specs]
  (let [ns (name ns)]
    `(do ~@(for [[nm effect] (partition 2 op-specs)]
             (cat-op-form (symbol ns (name nm)) effect)))))


(cat-ops dom

  ;;;; Traversal
  document-element  [    -- element]
  element-with-id   [id  -- element]
  elements-with-tag [tag -- elements]
  select    [selector -- elements]
  subselect [element selector -- element elements]
  parent    [element -- element parent]
  children  [element -- element children]

  ;;;; Attributes
  attribute         [element  attribute       -- element value]
  set-attribute     [elements attribute value -- elements]
  set-attributes    [elements attributes      -- elements]
  remove-attribute  [elements attribute       -- elements]
  remove-attributes [elements attributes      -- elements]

  ;;;; CSS

  ;;; Classes
  classes        [element          -- element  classes]
  has-class?     [element  class   -- element  bool]
  set-classes    [elements classes -- elements]
  add-class      [elements class   -- elements]
  add-classes    [elements classes -- elements]
  remove-class   [elements class   -- elements]
  remove-classes [elements classes -- elements]
  toggle-class   [elements class   -- elements]
  toggle-classes [elements classes -- elements]

  ;;; Styles
  style      [element  property       -- element value]
  set-style  [elements property value -- elements     ]
  set-styles [elements styles         -- elements     ]

  ;;;; Data
  all-data    [element            -- element data]
  get-data    [element  key       -- data        ]
  add-data    [elements key value -- elements    ]
  remove-data [elements key       -- elements    ]

  ;;;; Events
  bind [elements event-type key callback -- elements]
  unbind [key -- elements]

  ;;;; Manipulation
  create-element [tag -- element]
  append [parent elements -- parent]
  remove [elements --]

)
