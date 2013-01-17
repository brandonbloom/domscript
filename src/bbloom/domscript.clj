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
          cat/first
          (add-data :foo 123)
          (add-data :bar 456)
          (remove-data :foo)
          ;all-data cat/prn
          (get-data :bar) cat/prn
          ;(set-style "fill" "#cc00cc")
          ;(set-styles {:fill "#0000cc"})
          ;(remove-attribute :fill)
          ;cat/first (style :fill) cat/prn

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
