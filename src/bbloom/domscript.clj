(ns bbloom.domscript
  (:refer-clojure :exclude [remove])
  (:use [bbloom.domscript.cat])
  (:require [factjor.core :as cat]
            [bbloom.domscript.svg :as svg]
            [bbloom.domscript.core :refer (*document*)]))

(comment

  (svg/update-document!
    (fn [document]
      (binding [*document* document]
        (cat/run

          (elements-with-tag :svg/rect)
          (set-attribute :fill "blue")

          ;document-element
          ;cat/dup children remove

          ;(create-element :svg/rect)
          ;(toggle-class "foo")
          ;(toggle-classes #{"foo" "bar"})
          ;classes cat/prn
          ;(set-attributes {:id "the-rect"
          ;                 :x 30 :y 50
          ;                 :width 10 :height 30
          ;                 :fill "red"})
          ;append

  ))))

)
