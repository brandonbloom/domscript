(ns bbloom.domscript
  (:refer-clojure :exclude [remove])
  (:use [bbloom.domscript.cat])
  (:require [factjor.core :as cat]
            [bbloom.domscript.svg :as svg]
            [bbloom.domscript.core :refer (*document*)]))



(comment

  (def window (svg/create-window))

  (defn go [& code]
    (svg/update-document! window
      (fn [document]
        (binding [*document* document]
          (cat/run code cat/call)))))

  (go
    document-element
    (attribute :title) cat/prn
    (attribute :width) cat/prn
    (attribute :height) cat/prn
    ; Changing these ^^ doesn't affect the frame or canvas yet.
  )



  (go
    document-element
    children remove
  )

  (go
    document-element
    (create-element :svg/rect)
    (set-attributes {:id "the-rect"
                     :x 0 :y 0
                     :width 639 :height 479
                     :fill "red"})
    append
  )

    ;(elements-with-tag :svg/rect)
    ;cat/first
    ;(add-data :foo 123)
    ;(add-data :bar 456)
    ;(remove-data :foo)
    ;;all-data cat/prn
    ;(get-data :bar) cat/prn

    ;(set-style "fill" "#cc00cc")
    ;(set-styles {:fill "#0000cc"})
    ;(remove-attribute :fill)
    ;cat/first (style :fill) cat/prn

)
