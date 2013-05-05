(ns bbloom.domscript
  (:refer-clojure :exclude [remove])
  (:use [bbloom.domscript.cat])
  (:require [factjor.core :as cat]
            [bbloom.domscript.svg :as svg]
            [bbloom.domscript.core :refer (*window*)]))



(comment

  (def window (svg/create-window))

  (defn go [& code]
    (svg/send window
      #(binding [*window* window]
         (apply cat/run code))))

  (go
    document-element
    (attribute :title) cat/prn
    (attribute :width) cat/prn
    (attribute :height) cat/prn
    ;TODO Changing these ^^ doesn't affect the frame or canvas yet.
  )

  (go
    document-element
    descendents cat/prn)

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

  (defn random-rect []
    (let [w (+ (rand-int 75) 25)
          h (+ (rand-int 75) 25)
          x (rand-int (- 640 w))
          y (rand-int (- 480 h))
          c (rand-nth ["red" "green" "blue" "yellow" "orange"])]
      [(create-element :svg/rect)
       (set-attributes {:x x :y y :width w :height h :fill c})
       append]))

  (go
    document-element
    (apply concat (repeatedly 5 random-rect)) cat/call)

  (go
    (select "rect[fill=red]")
    (set-attribute :stroke-width 3)
    (set-attribute :stroke "black")
    (bind :click ::foo (fn [event] (prn event)))
    )

  (go
    (unbind ::foo))

  (:handlers window)

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
