(ns ui.core
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as string :refer [split-lines split join]]
            [ui.shapes :as shapes :refer [tri square pent hex hept oct 
                                          arc-half
                                          b1 b2 b3 b4]]
            [ui.fills :as fills :refer
              [ gray
                mint
                midnight
                navy
                blue
                orange
                br-orange
                pink
                white
                yellow
                red
                purple ]]
            [ui.generators :refer
             [draw
              freak-out
              gen-circ
              gen-group
              gen-line
              gen-poly
              gen-rect
              gen-shape
              gen-offset-lines
              gen-bg-lines
              gen-grid
              gen-line-grid
              gen-cols
              gen-rows
              gen-mask]]
            [ui.filters :as filters :refer [turb noiz soft-noiz disappearing splotchy blur]]
            [ui.patterns :as patterns :refer
             [ gen-color-noise
               pattern
               pattern-def
               sized-pattern-def
               blue-dots
               blue-lines
               pink-dots
               pink-lines
               gray-dots
               gray-dots-lg
               gray-lines
               gray-patch
               mint-dots
               mint-lines
               navy-dots
               navy-lines
               orange-dots
               orange-lines
               br-orange-dots
               br-orange-lines
               yellow-dots
               yellow-lines
               white-dots
               white-dots-lg
               white-lines
               shadow
               noise
               pink-scale-dots
               pink-scale-lines]]
            [ui.animations :as animations :refer
              [ make-body
                splice-bodies
                make-frames!
                nth-frame
                even-frame
                odd-frame]]))

(enable-console-print!)

(println "Loaded.")

;; hides heads up display for performance
(defn hide-display [] (let [heads-up-display (.getElementById js/document "figwheel-heads-up-container")]
  (.setAttribute heads-up-display "style" "display: none")
))

;; ------------------------ SETTINGS  ---------------------

(def width (atom (.-innerWidth js/window)))
(def height (atom (.-innerHeight js/window)))

(def settings {:width @width
               :height @height })

(defonce frame (atom 0))

;; -------------------- HELPERS ---------------------------

(defn sin [x] (.sin js/Math x))
(defn cos [x] (.cos js/Math x))

(defn style
  [changes shape]
  (update-in shape [:style] #(merge % changes)))

(defn url
  ([ fill-id ]
    (str "url(#" fill-id ")")))

(defn val-cyc
  [frame vals]
  (let [n (count vals)]
    (nth vals (mod frame n))))

(defn seconds-to-frames
  [seconds]
  (* 2 seconds))

(defonce ran (atom {}))

(defn anim-and-hold
  [name frame duration fader solid]
  (let [init-frame (@ran name)
        ran? (and init-frame (<= (+ init-frame (seconds-to-frames duration)) frame))
        ret (if ran? solid fader)]
    (when-not init-frame (swap! ran assoc name frame))
    ret))


;; -------------------- SHAPE ANIMATION HELPER ---------------------------

(defn anim
  ([name duration count shape] (anim name duration count {} shape))
  ([name duration count opts shape]
  (let [animations
    { :animation-name name
      :animation-fill-mode "forwards"
      :animation-duration duration
      :animation-iteration-count count
      :animation-delay (or (:delay opts) 0)
      :animation-timing-function (or (:timing opts) "ease")}]
          (update-in shape [:style] #(merge % animations)))))

;; ----------- ANIMATIONS ----------------

;; syntax reminder
; (make-frames!
;   "NAME"
;   [frames]
;   (make-body "ATTRIBUTE" [values]))

; (trans x y)
; (nth-frame num FRAME)
; (even-frame FRAME)
; (odd-frame FRAME)

; "fade-in-out" "fade-out" "wee-oo" "rot" "rev"

;; --------------- ANIMATIONS STORAGE --------------------

(defn back-and-forth!
  [name start-str finish-str]
  (make-frames! name [0 50 100]
    (make-body "transform" [
      (str start-str)
      (str finish-str)
      (str start-str)])))

(defn a-to-b!
  [name att start-str finish-str]
  (make-frames! name [0 100]
    (make-body att [
      (str start-str)
      (str finish-str)])))

(make-frames! "etof" [0 100] (make-body "transform" ["translateY(10px)" "translateY(1000px)"]))

(back-and-forth! "scaley" "scale(1)" "scale(15)")
(back-and-forth! "scaley-huge" "scale(20)" "scale(50)")


(a-to-b! "new-fi" "fill-opacity" "0" ".5")
(a-to-b! "sc-rot" "transform" "scale(4) rotate(0deg)" "scale(30) rotate(-80deg)")
(a-to-b! "slide-up" "transform" "translateY(125%)" (str "translateY("(* 0.15 @height)")"))
(a-to-b! "grow2to3" "transform" "rotate(280deg) scale(1)" "rotate(280deg) scale(1.2)")

(defn fade-start!
  [name op-end]
  (make-frames! name [0 99 100]
    (make-body "fill-opacity" [
      (str 0)
      (str 0)
      (str op-end)])))

(fade-start! "fi" 1)

(make-frames!
  "woosh"
    [10, 35, 55, 85, 92]
   (make-body "transform" [
                           "translate(80vw, 50vh) rotate(2deg) scale(1.2)"
                           "translate(60vw, 60vh) rotate(-200deg) scale(2.4)"
                           "translate(40vw, 40vh) rotate(120deg) scale(3.4)"
                           "translate(20vw, 30vh) rotate(-210deg) scale(2.2)"
                           "translate(60vw, 80vh) rotate(400deg) scale(6.2)"]))

(make-frames!
  "woosh-2"
    [10, 35, 55, 85, 92]
   (make-body "transform" [
                           "translate(80vw, 50vh) rotate(2deg) scale(11.2)"
                           "translate(60vw, 60vh) rotate(-200deg) scale(12.4)"
                           "translate(40vw, 40vh) rotate(120deg) scale(13.4)"
                           "translate(20vw, 30vh) rotate(-210deg) scale(12.2)"
                           "translate(60vw, 80vh) rotate(400deg) scale(6.2)"]))


(make-frames!
  "woosh-3"
    [10, 55, 85, 92]
   (make-body "transform" [
                           "translate(80vw, 10vh) rotate(2deg) scale(2.2)"
                           "translate(40vw, 40vh) rotate(120deg) scale(8.4)"
                           "translate(50vw, 30vh) rotate(0deg) scale(12.2)"
                           "translate(60vw, 80vh) rotate(400deg) scale(4.2)"]))
(make-frames!
  "woosh-4"
    [10, 35, 55, 85, 92]
   (make-body "transform" [
                           "translate(80vw, 10vh) rotate(2deg) scale(2.2)"
                           "translate(40vw, 40vh) rotate(220deg) scale(10.4)"
                           "translate(50vw, 30vh) rotate(0deg) scale(4.2)"
                           "translate(50vw, 30vh) rotate(-300deg) scale(2.2)"
                           "translate(60vw, 80vh) rotate(400deg) scale(1.2)"]))


 (make-frames!
   "loopy-left"
     [10, 35, 55, 85, 92]
    (make-body "transform" [
                            "translate(90vw, 10vh) rotate(2deg) scale(2.2)"
                            "translate(80vw, 30vh) rotate(220deg) scale(6.4)"
                            "translate(60vw, 40vh) rotate(0deg) scale(4.2)"
                            "translate(30vw, 80vh) rotate(-300deg) scale(2.2)"
                            "translate(10vw, 90vh) rotate(400deg) scale(3.2)"]))

(make-frames!
   "loopy-right"
     [10, 35, 55, 85, 92]
    (make-body "transform" [
                            "translate(10vw, 10vh) rotate(2deg) scale(2.2)"
                            "translate(30vw, 80vh) rotate(220deg) scale(6.4)"
                            "translate(60vw, 40vh) rotate(0deg) scale(4.2)"
                            "translate(80vw, 30vh) rotate(-300deg) scale(2.2)"
                            "translate(90vw, 90vh) rotate(400deg) scale(3.2)"]))

(make-frames!
 "dashy"
 [100]
 (make-body "stroke-dashoffset" [0]))

(make-frames!
 "morph"
  [0 15 30 45 60 75 100]
 (make-body "d" [
  (str "path('"tri"')")
  (str "path('"square"')")
  (str "path('"pent"')")
  (str "path('"hex"')")
  (str "path('"hept"')")
  (str "path('"oct"')")
  (str "path('"tri"')")
]))


;; --------------- ATOMS STORAGE --------------------

(def drops
  (atom  (map
     #(->>
       (gen-rect white (+ 30 (* % 160)) 10 200 36)
       (anim "etof" "1.2s" "infinite" {:delay (str (* .5 %) "s")})
       (draw))
     (range 10))))


(def move-me
  (->>
   (gen-shape white hept)
   (style {:opacity 1 :transform-origin "center" :transform "scale(4.4)"})
   (style {:filter (url (:id noiz))})
   (anim "woosh-4" "3s" "infinite")
   (draw)
   (atom)))

(def move-me-2
  (->>
   (gen-shape pink hept)
   (style {:opacity 1 :transform-origin "center" :transform "scale(4.4)"})
   (style {:filter (url (:id noiz))})
   (anim "woosh-4" "6s" "infinite")
   (draw)
   (atom)))

(def move-me-3
  (->>
   (gen-shape (pattern (:id mint-dots)) hept)
   (style {:opacity 1 :transform-origin "center" :transform "scale(4.4)"})
   (style {:filter (url (:id noiz))})
   (style {:mix-blend-mode "exclusion"})
   (anim "woosh-4" "4s" "infinite")
   (draw)
   (atom)))

(def bg (->>
  (gen-circ (pattern (str "noise-" navy)) (* .5 @width) (* .5 @height) 1800)
  (style {:opacity 1 :transform-origin "center" :transform "scale(4)"})
  (anim "sc-rot" "3s" "infinite" {:timing "linear"})
  (draw)
  (atom)))






;; ------------------- DRAWING HELPERS ------------------------

;(doall (map deref levels))
(def levels
  (map-indexed
    (fn [idx color]
          (->>
            (gen-rect color -100 -100 "120%" "120%" (url "cutout"))
            (style {:opacity .4
                    :transform-origin "center"
                    :transform (str
                                "translate(" (- (rand-int 200) 100) "px, " (- (rand-int 300) 100) "px)"
                                "rotate(" (- 360 (rand-int 720)) "deg)")})
            (anim "fade-in-out" "10s" "infinite" {:delay (str (* .1 idx) "s")})
            (draw)
            (atom)))
    (take 10 (repeatedly #(nth [orange pink white yellow] (rand-int 6))))))



 ;; ----------- COLLECTION SETUP AND CHANGE ----------------


#_(println (doall (map 
        #(->>
          (gen-circ white 19 (+ 10 (* % 19)) (+ 2 (- 4 %)))
          (draw))
        (range 10))))

(defonce collection (atom (list)))
;(reset! ran {})


(defn cx [frame]
  (list

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;;;;;;;;;;;;;;;;;; BACKGROUNDS ;;;;;;;;;;;;;;;;;;;;;;;
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  (let
    [colors [
      mint mint mint mint
      ;yellow
             ;"#000"

             ]]
      (->>
        (gen-rect (val-cyc frame colors) 0 0 "100vw" "100%")
        (style {:filter (url (:id noiz))})
        (style {:transform "scale(11)"})
        (style {:opacity .5})
        (draw)))


  ;; As
  
  #_(gen-group {}
             (gen-group {:style {:transform "translate(2vw, 5vh)"}}
                        (->>
                          (gen-shape yellow tri)
                            (style {:transform "rotateY(35deg) scale(.7)"})
                            (draw)
                            (when (nth-frame 1 frame)))
                        (->>
                          (gen-line ["8.5vw" "12vh"] ["8.5vw" "16.5vh"] red 8)
                          (draw)
                          (when (nth-frame 1 frame))))
             
             (gen-group {:style {:transform "translate(16vw, 5vh)"}}
                        (->>
                          (gen-shape mint tri)
                            (style {:transform "rotateY(35deg) scale(.7)"})
                            (draw)
                            (when (nth-frame 1 frame)))
                        (->>
                          (gen-line ["8.5vw" "12vh"] ["8.5vw" "16.5vh"] yellow 8)
                          (draw)
                          (when (nth-frame 1 frame)))
                        (->>
                          (gen-circ midnight "8.5vw" "12vh" 14)
                          (style {:transform "translate(-1px, -1px)"})
                          (draw)
                          (when (nth-frame 1 frame)))
                        (->>
                          (gen-circ pink "8.5vw" "12vh" 14)
                          (draw)
                          (when (nth-frame 1 frame))))
             
             (gen-group {:style {:transform "translate(30vw, 5vh)"}}
                        (->>
                          (gen-shape red tri)
                            (style {:transform "rotateY(35deg) scale(.7)"})
                            (draw)
                            (when (nth-frame 1 frame)))
                        (->>
                          (gen-line ["8.5vw" "12vh"] ["8.5vw" "16.5vh"] yellow 8)
                          (draw)
                          (when (nth-frame 1 frame)))))
  
  ;; Bs
  #_(gen-group {:style {:transform "translateY(200px)"}}
             
             (gen-group {:style {:transform "translate(5vw, 5vh)"}}
              (->>
                (gen-rect blue 10 10 120 120 (url "b"))
                (draw)
                (when (nth-frame 1 frame)))
                        
              (->>
                (gen-rect (pattern (:id navy-lines)) 10 10 120 120 (url "b"))
                (draw)
                (when (nth-frame 1 frame)))
                  
              (->>
                (gen-line [10 30] [50 30] yellow 8)
                (style {:transform "translate(1px, 2px)"})
                (style {:mix-blend-mode "color-burn"})
                (draw)
                (when (nth-frame 1 frame)))
                        
              (->>
                (gen-line [10 30] [50 30] yellow 8)
                (draw)
                (when (nth-frame 1 frame)))
                        
              (->>
                (gen-line [8 70] [50 70] yellow 8)
                (style {:transform "translate(3px, 3px)"})
                (style {:mix-blend-mode "color-burn"})
                (draw)
                (when (nth-frame 1 frame)))

              (->>
                (gen-line [10 70] [50 70] yellow 8)
                (draw)
                (when (nth-frame 1 frame))))
             
             
             
              (gen-group {:style {:transform "translate(20vw, 5vh)"}}
               (->>
                 (gen-rect pink 10 10 120 120 (url "b"))
                 (draw)
                 (when (nth-frame 1 frame)))
                         
                   
               (->>
                 (gen-line [10 30] [50 30] mint 8)
                 (style {:transform "translate(1px, 2px)"})
                 (style {:mix-blend-mode "color-burn"})
                 (draw)
                 (when (nth-frame 1 frame)))
                         
               (->>
                 (gen-line [10 30] [50 30] mint 8)
                 (draw)
                 (when (nth-frame 1 frame)))
                         
               (->>
                 (gen-line [8 70] [50 70] mint 8)
                 (style {:transform "translate(3px, 3px)"})
                 (style {:mix-blend-mode "color-burn"})
                 (draw)
                 (when (nth-frame 1 frame)))

               (->>
                 (gen-line [10 70] [50 70] mint 8)
                 (draw)
                 (when (nth-frame 1 frame))))
             
             
             (gen-group {:style {:transform "translate(35vw, 5vh)"}}
              (->>
                (gen-rect yellow 10 10 120 120 (url "b"))
                (draw)
                (when (nth-frame 1 frame)))
                        
                
                        
              (->>
                (gen-line [10 30] [50 30] red 8)
                (draw)
                (when (nth-frame 1 frame)))


              (->>
                (gen-line [10 70] [50 70] red 8)
                (draw)
                (when (nth-frame 1 frame))))
             
             
             
             )
  
  
  ;; Cs
  (gen-group {:style {:transform "translateY(166px)"}}

             
    (gen-group {}
               (->>
                 (gen-shape purple arc-half)
                   (style {:transform-origin "center" :transform "translate(120px, 12px) scale(1) rotateZ(65deg)"})
                   (draw)
                   (when (nth-frame 1 frame)))
              
              (->>
                (gen-shape mint arc-half)
                  (style {:transform-origin "center" :transform "translate(120px, 68px) scale(1) rotateZ(-65deg)"})
                  (draw)
                  (when (nth-frame 1 frame)))
              
                (->>
                  (gen-shape pink arc-half)
                    (style {:transform-origin "center" :transform "translate(100px, 40px) scale(1)"})
                    (draw)
                    (when (nth-frame 1 frame))))
             
             
    #_(gen-group {:style {:transform "translate(15vw)"}}
                             (->>
                               (gen-shape red arc-half)
                                 (style {:transform-origin "center" :transform "translate(120px, 12px) scale(1) rotateZ(65deg)"})
                                 (draw)
                                 (when (nth-frame 1 frame)))
                            
                            (->>
                              (gen-shape red arc-half)
                                (style {:transform-origin "center" :transform "translate(120px, 68px) scale(1) rotateZ(-65deg)"})
                                (draw)
                                (when (nth-frame 1 frame)))
                            
                              (->>
                                (gen-shape red arc-half)
                                  (style {:transform-origin "center" :transform "translate(100px, 40px) scale(1)"})
                                  (draw)
                                  (when (nth-frame 1 frame))))
               
               
   #_(gen-group {:style {:transform "translate(30vw)"}}
                            (->>
                              (gen-shape blue arc-half)
                                (style {:transform-origin "center" :transform "translate(120px, 12px) scale(1) rotateZ(65deg)"})
                                (draw)
                                (when (nth-frame 1 frame)))
                           
                           (->>
                             (gen-shape blue arc-half)
                               (style {:transform-origin "center" :transform "translate(120px, 68px) scale(1) rotateZ(-65deg)"})
                               (draw)
                               (when (nth-frame 1 frame)))
                           
                             (->>
                               (gen-shape blue arc-half)
                                 (style {:transform-origin "center" :transform "translate(100px, 40px) scale(1)"})
                                 (draw)
                                 (when (nth-frame 1 frame))))
               
             
             
             
             )
  
  
  ;; Ds
  (gen-group {:style {:transform "translateY(190px)"}}
             
             (gen-group {:style {:transform "translateX(28vw)"}
                         :mask (url "d") }
            
                (->>
                  (gen-rect yellow 10 10 120 120)
                  (draw)
                  (when (nth-frame 1 frame)))
                        
                  (freak-out 10 120
                             10 120
                             10
                             20
                             red)
                        
                (->>
                  (gen-line [10 70] [80 70] mint 8)
                  (draw)
                  (when (nth-frame 1 frame))))
             
             
            #_(gen-group {:style {:transform "translateX(19vw)"}
                        :mask (url "d") }
           
               (->>
                 (gen-rect purple 10 10 120 120)
                 (draw)
                 (when (nth-frame 1 frame)))
                       
               (->>
                 (gen-line [10 70] [80 70] pink 8)
                 (draw)
                 (when (nth-frame 1 frame))))
             
           #_(gen-group {:style {:transform "translateX(35vw)"}
                       :mask (url "d") }
          
              (->>
                (gen-rect blue 10 10 120 120)
                (draw)
                (when (nth-frame 1 frame)))
                      
                (->>
                  (gen-rect (pattern (:id navy-lines)) -10 -10 140 180)
                  (style {:transform "rotate(15deg)"})
                  (draw)
                  (when (nth-frame 1 frame)))
                      
              (->>
                (gen-line [10 70] [80 70] pink 8)
                (draw)
                (when (nth-frame 1 frame))))
             
             )
  
  
  ;; Es
  (gen-group {:style {:transform "translateY(200px)"}}
             
             (gen-group {:style {:transform "translateX(48vw)"}}
                        (->>
                          (gen-rect pink 10 10 120 120 (url "e"))
                          (draw))
                        (->>
                          (gen-line [60 42] [96 42] yellow 8)
                          (style {:transform "translate(-1px, 6px)"})
                          (style {:mix-blend-mode "color-burn"})
                          (draw)
                          (when (nth-frame 1 frame)))
                        (->>
                          (gen-line [60 42] [96 42] yellow 8)
                          (draw)
                          (when (nth-frame 1 frame)))
                        (->>
                          (gen-line [60 66] [96 66] yellow 8)
                          (style {:transform "translate(-1px, 6px)"})
                          (style {:mix-blend-mode "color-burn"})
                          (draw)
                          (when (nth-frame 1 frame)))
                        (->>
                          (gen-line [60 66] [96 66] yellow 8)
                          (draw)
                          (when (nth-frame 1 frame)))))
  
  ;; Is
  (gen-group {:style {:transform "translateY(200px)"}}
    (gen-group {:style {:transform "translateX(40vw)"}}
               
               (->>
                 (gen-rect mint 10 10 50 110)
                 (draw))
               
               (->>
                 (gen-rect blue 10 10 50 20)
                 (draw))
  
               ))
  
  ;; Os
  (gen-group {:style {:transform "translateY(200px)"}}
    (gen-group {:style {:transform "translateX(16vw)"}}
               
               (->>
                 (gen-circ blue 60 60 60)
                 (draw))
               
               (->>
                 (gen-circ purple 60 60 8)
                 (draw)))
             
     #_(gen-group {:style {:transform "translateX(44vw)"}}
                
                (->>
                  (gen-circ red 60 60 60)
                  (draw))
                
                (->>
                  (gen-circ yellow 60 60 8)
                  (draw))))
  
  ;; Ns
  #_(gen-group {:style {:transform "translateY(200px)"}}
             (gen-group {:style {:transform "translate(5vw, 5vh)"}}
                        (->>
                          (gen-poly yellow [10 10 
                                          68 46 
                                          68 10 
                                          100 10
                                          100 120 
                                          10 120])
                          (draw))
                        (->>
                          (gen-line [38 90] [38 120] red 8)
                          (draw)
                          (when (nth-frame 1 frame))))
             
             (gen-group {:style {:transform "translate(19vw, 5vh)"} 
                         :mask (url "n")}
                        (->>
                          (gen-rect purple 10 10 100 110)
                          (draw)
                          (when (nth-frame 1 frame)))
                       
                          (freak-out 10 100
                                     10 110
                                     10
                                     14
                                     mint)
                        (->>
                          (gen-line [38 90] [38 120] red 8)
                          (draw)
                          (when (nth-frame 1 frame))))
             
               (gen-group {:style {:transform "translate(34vw, 5vh)"}}
                          (->>
                            (gen-poly pink [10 10 
                                            68 46 
                                            68 10 
                                            100 10
                                            100 120 
                                            10 120])
                            (draw))
                          (->>
                            (gen-line [38 90] [38 120] yellow 8)
                            (draw)
                            (when (nth-frame 1 frame)))))

  

  

)) ; cx end

;(defonce collection (atom (list (cx 1))))



;; ----------- LOOP TIMERS ------------------------------

(defonce start-cx-timer
  (js/setInterval
    #(reset! collection (cx @frame)) 50))

(defonce start-frame-timer
  (js/setInterval
    #(swap! frame inc) 500))


;; ----------- DEFS AND DRAW ------------------------------

(def gradients [[:linearGradient { :id "grad" :key (random-uuid)}
                 [:stop { :offset "0" :stop-color "white" :stop-opacity "0" }]
                 [:stop { :offset "1" :stop-color "white" :stop-opacity "1" }]]])


(def mask-list [
            [ "poly-mask"
              [:path {:d b2 :fill "#fff" :style { :transform-origin "center" :animation "woosh 2s infinite"}}]]
            [ "poly-mask-2"
                          [:path {:d b3 :fill "#fff" :style { :transform-origin "center" :animation "woosh-3 3s infinite"}}]]
            [ "grad-mask"
              [:circle { :cx (* 0.5 @width) :cy (* 0.5 @height) :r 260 :fill "url(#grad)" }]]
            [ "cutout"
             (->>
               (gen-rect white 10 12 (* 0.94 @width) (* 0.88 @height))
               (draw))
             (->>
               (gen-circ "#000" (* 0.7 @width) (* 0.7 @height) 100)
                (draw))]
              ["rect-buds"
               (->>
                 (gen-rect white 10 12 (* 0.3 @width) (* 0.5 @height))
                 (draw))
                 ]
                
              
                ["nn" [ :image {:key (random-uuid) :x "100" :y "200" :width "100%" :height "100%" :xlinkHref "img/blop.png" :style {:transform-origin "center" :transform "scale(10)" :animation "woosh 6s infinite"} }]]
                
                
                ;; ALPHA MASKS
                ["b"
                  (gen-group {}(->>
                    (gen-circ white 63 36 26)
                    (style {:opacity 1})
                    (draw))
                  (->>
                    (gen-rect white 10 10 60 50)
                    (draw))
                  (->>
                    (gen-circ white 68 74 26)
                    (style {:opacity 1})
                    (draw))
                  (->>
                    (gen-rect white 10 50 60 50)
                    (draw)))
                  ]
                ["d" 
                  (gen-group {}(->>
                    (gen-circ white 66 67 60)
                    (style {:opacity 1})
                    (draw))
                  (->>
                    (gen-rect white 10 10 69 117)
                    (draw)))]
                ["e"
                 (->>
                   (gen-poly white [10 10 
                                   110 10
                                   110 40
                                   95 40
                                   95 70
                                   110 70
                                   110 120
                                   10 120])
                   (draw))]
                ["n"
                 (->>
                   (gen-poly white [10 10 
                                   68 46 
                                   68 10 
                                   100 10
                                   100 120 
                                   10 120])
                   (draw))]

            ])



(def masks (map (fn [[id & rest]] (apply gen-mask id rest)) mask-list))


(def all-filters [turb noiz soft-noiz disappearing splotchy blur])
(def all-fills [gray mint navy blue orange br-orange pink white yellow midnight])


(defn drawing []
  [:svg {
    :style  {:mix-blend-mode
             (val-cyc @frame
                      ["multiply" "multiply"
                       ]) }
    :width  (:width settings)
    :height (:height settings)
         ;:width 100
         ;:height 100
         }
     ;; filters
    (map #(:def %) all-filters)
    ;; masks and patterns
    [:defs
     noise
     (map identity gradients)
     (map identity masks)
     (map gen-color-noise all-fills)
     (sized-pattern-def pink-scale-dots 20 105)
     (sized-pattern-def pink-scale-lines 200 200)
     (map pattern-def [ blue-dots
                        blue-lines
                        pink-dots
                        pink-lines
                        gray-dots
                        gray-dots-lg
                        gray-lines
                        gray-patch
                        mint-dots
                        mint-lines
                        navy-dots
                        navy-lines
                        orange-dots
                        orange-lines
                        br-orange-dots
                        br-orange-lines
                        yellow-dots
                        yellow-lines
                        white-dots
                        white-dots-lg
                        white-lines
                        shadow
                        pink-scale-dots
                        pink-scale-lines ])]

    ;; then here dereference a state full of polys
    @collection

    ])

(reagent/render-component [drawing]
                          (js/document.getElementById "app-container"))

;(hide-display)
