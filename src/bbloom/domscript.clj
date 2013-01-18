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
                     :x 250 :y 75
                     :width 100 :height 50
                     :fill "red"})
    append
  )

  (go
    (select "#the-rect")
    cat/count cat/prn)

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
