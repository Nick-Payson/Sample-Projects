;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-intermediate-lambda-reader.ss" "lang")((modname |hw11 finished|) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f () #f)))
;; HW11
(require racket/string)

; A Graph is a (make-graph [List-of Symbol] [Symbol -> [List-of Symbol]])
(define-struct graph [nodes neighbors])
; and represents the nodes and edges in a graph.
; All of the symbols in nodes are assumed to be unique, as are the symbols in
; any list returned by neighbors, and all of the symbols returned by neighbors
; are assumed to be in nodes.

;; Examples:
(define graph1 (make-graph '(a b c d e f) (λ (sym) (cond [(symbol=? sym 'a) '(b c d)]
                                                         [(symbol=? sym 'c) '(d)]
                                                         [(symbol=? sym 'f) '(a b c d e)]
                                                         [else '()]))))
(define graph2 (make-graph '(z y x w) (λ (sym) (cond [(symbol=? sym 'z) '(y)]
                                                     [(symbol=? sym 'y) '(z x)]
                                                     [(symbol=? sym 'x) '(y w)]
                                                     [(symbol=? sym 'w) '(x)]))))
(define graph3 (make-graph '(z y x w)
                           (λ (sym) (cond [(symbol=? sym 'z) '(y)]
                                          [(symbol=? sym 'y) '(x)]
                                          [(symbol=? sym 'x) '(w)]
                                          [(symbol=? sym 'w) '()]))))
(define graph4 (make-graph '(a b c d e f g h i) (λ (sym) (cond [(symbol=? sym 'a) '(b c d)]
                                                               [(symbol=? sym 'c) '(d)]
                                                               [(symbol=? sym 'f) '(a b c d e)]
                                                               [else '()]))))


;; Testing pattern:
; make-graph=? : Graph -> [Graph -> Boolean]
; Takes a graph and produces a function that checks if
; its argument is graph=? to the original graph
(define make-graph=? (λ (g1) (λ (g2) (graph=? g1 g2))))
 
; f : Graph ... -> Graph
; Do something to g
#;(define (f g ...) ...)
 
#;(check-satisfied (f some-input-graph ...)
                   (make-graph=?
                    some-expected-graph))

;; from HW10:
;; ---------------------------------------------------------------------------------------------------
;; neighbor-of? : Graph Symbol Symbol -> Boolean
;; Is the second symbol a neighbor of the first symbol?
;; assume both symbols are in the graph
#|
(check-expect (neighbor-of? graph1 'a 'b) #t)
(check-expect (neighbor-of? graph1 'd 'c) #f)
(check-expect (neighbor-of? graph2 'x 'z) #f)
(check-expect (neighbor-of? graph2 'w 'x) #t)
(check-expect (neighbor-of? graph2 'y 'z) #t)
(check-expect (neighbor-of? graph2 'y 'w) #f)
|#
(define (neighbor-of? g s1 s2)
  (local [(define neighbors ((graph-neighbors g) s1))]
    (ormap (λ (sym) (symbol=? sym s2)) neighbors)))

;; both-neighbors : Graph Symbol Symbol -> [List-of Symbol]
;; Returns list of the neighbors of both symbols, without duplicates
#|
(check-expect (both-neighbors graph2 'x 'w) '(y w x))
(check-expect (both-neighbors graph1 'a 'c) '(b c d))
(check-expect (both-neighbors graph1 'b 'd) '())
(check-expect (both-neighbors graph2 'y 'z) '(z x y))
(check-expect (both-neighbors graph2 'y 'x) '(z x y w))
(check-expect (both-neighbors graph2 'y 'y) '(z x))
|#
(define (both-neighbors g s1 s2)
  (local [(define s1-neigh ((graph-neighbors g) s1))
          (define s2-neigh ((graph-neighbors g) s2))]
    (append s1-neigh (filter (λ (elem) (not (neighbor-of? g s1 elem))) s2-neigh))))

;; graph=? : Graph Graph -> Boolean
;; Are the two graphs equal?
#|
(check-expect (graph=? graph1 graph1) #t)
(check-expect (graph=? graph1 graph2) #f)
(check-expect (graph=? graph2 graph2) #t)
(check-expect (graph=? graph2 graph3) #f)
(check-expect (graph=? graph1 graph4) #f)
(check-expect (graph=? graph2 (make-graph '(x z y w) (λ (sym) (cond [(symbol=? sym 'z) '(y)]
                                                                    [(symbol=? sym 'y) '(z x)]
                                                                    [(symbol=? sym 'x) '(y w)]
                                                                    [(symbol=? sym 'w) '(x)])))) #t)
|#
(define (graph=? g1 g2) 
  (and (nodes=? (graph-nodes g1) (graph-nodes g2))
       (neighbors=? g1 g2)))

;; nodes=? : [List-of Symbol] [List-of Symbol] -> Boolean
;; Do the two lists contain the same symbols?
#|
(check-expect (nodes=? '(z y x w) '(w x y z)) #t)
(check-expect (nodes=? '(a b c) '(a b d)) #f)
(check-expect (nodes=? '(a b c) '(a b c)) #t)
(check-expect (nodes=? '(c b a) '(a c b)) #t)
(check-expect (nodes=? '(word words somanywords)
                       '(somanywords word words wordy)) #f)
(check-expect (nodes=? '() '()) #t)
(check-expect (nodes=? '() '(actual symbols)) #f)
(check-expect (nodes=? (graph-nodes graph2) (graph-nodes graph3)) #t)
|#
(define (nodes=? n1 n2)
  (local [;; contains? : [List-of Symbol] Symbol -> Boolean
          ;; Does the symbol exist in the list?
          (define (contains? los sym)
            (ormap (λ (elem) (symbol=? elem sym)) los))
          ;; subset? : [List-of Symbol] [List-of Symbol] -> Boolean
          ;; Do all the symbols in list2 exist in list1?
          (define (subset? list1 list2)
            (andmap (λ (elemof2) (contains? list1 elemof2)) list2))]
    (and (subset? n1 n2) (subset? n2 n1))))

;; neighbors=? : Graph Graph -> Boolean
;; Are the two graphs' "neighbors" functions equal?
;; Assume the two graphs' "nodes" lists are equal
;; (because this will be called in graph=?, and if the nodes are not equal, it will short-circuit)
#|
(check-expect (neighbors=? graph1 graph1) #t)
(check-expect (neighbors=? graph2 graph2) #t)
(check-expect (neighbors=? graph2 graph3) #f)
|#
(define (neighbors=? g1 g2)
  (local [(define g1-nodes (graph-nodes g1))
          (define g1-neigh (graph-neighbors g1))
          (define g2-neigh (graph-neighbors g2))]
    (andmap (λ (n) (nodes=? (g1-neigh n) (g2-neigh n))) g1-nodes)))
;; For each node in g1-nodes, we determine whether its neighbors (represented by a list of nodes)
;; are the same in g1 and g2. If they are the same for every node, this means the
;; _neighbors_ functions of g1 and g2 are the same.
;; Since we assume that every node in g1's _nodes_ is in g2's _nodes_ and vice versa,
;; we can simply call both graph's _neighbors_ functions on the nodes of g1.

;; collapse : Symbol Symbol Symbol Graph -> Graph
;; "collapses" the first two given symbols in the graph and replaces them with the third,
;; simplifying connections as necessary in the new graph's 'neighbors' function
(define (collapse old1 old2 new graph)
  (make-graph (cons new (remove old1 (remove old2 (graph-nodes graph))))
              ;; list of symbols in the new graph, the old list without the nodes to be collapsed
              ;; combined with the new node
              (lambda (input_node)
                (cond [(symbol=? new input_node)
                       ;; Want neighbors for new node, they are the neighbors for the old nodes
                       ;; with any references to collapsed nodes removed
                       (remove old2 (remove old1 (both-neighbors graph old1 old2)))]
                      [(not (symbol=? new input_node))
                       ;; Want neighbors for a non-new node, they are its neighbors as determined by
                       ;; the old neighbors function with references to since-collapsed nodes changed
                       ;; to the new node
                       (map (lambda (node)
                              (if (or (symbol=? old1 node) (symbol=? old2 node)) new node))
                            ((graph-neighbors graph) input_node))]))))
#|
(check-satisfied (collapse 'z 'y 'a graph2)
                 (make-graph=? (make-graph '(a x w)
                                           (lambda (sym) (cond [(symbol=? sym 'a) '(x)]
                                                               [(symbol=? sym 'x) '(a w)]
                                                               [(symbol=? sym 'w) '(x)])))))

(check-satisfied (collapse 'y 'w 'c graph3)
                 (make-graph=? (make-graph '(c z x)
                                           (lambda (sym) (cond [(symbol=? sym 'c) '(x)]
                                                               [(symbol=? sym 'x) '(c)]
                                                               [(symbol=? sym 'z) '(c)])))))

(check-satisfied (collapse 'g 'h 'q graph4)
                 (make-graph=? (make-graph '(a b c d e f q i)
                                           (lambda (sym) (cond [(symbol=? sym 'a) '(b c d)]
                                                               [(symbol=? sym 'c) '(d)]
                                                               [(symbol=? sym 'f) '(a b c d e)]
                                                               [else '()])))))
|#

;; ---------------------------------------------------------------------------------------------------

;; Exercise 1
;; reverse-edges : Graph -> Graph
;; Reverses the connections between nodes in the graph
(check-satisfied (reverse-edges graph1) (make-graph=?
                                         (make-graph '(a b c d e f)
                                                     (λ (sym) (cond [(symbol=? sym 'a) '(f)]
                                                                    [(symbol=? sym 'b) '(a f)]
                                                                    [(symbol=? sym 'c) '(a f)]
                                                                    [(symbol=? sym 'd) '(a c f)]
                                                                    [(symbol=? sym 'e) '(f)]
                                                                    [else '()])))))
(check-satisfied (reverse-edges graph3) (make-graph=? (make-graph
                                                       '(z y x w)
                                                       (λ (sym) (cond [(symbol=? sym 'z) '()]
                                                                      [(symbol=? sym 'y) '(z)]
                                                                      [(symbol=? sym 'x) '(y)]
                                                                      [(symbol=? sym 'w) '(x)])))))
(define (reverse-edges g)
  (local [(define g-nodes (graph-nodes g))]
    (make-graph g-nodes (λ (sym) (filter (λ (n) (neighbor-of? g n sym)) g-nodes)))))

;; Exercise 2
;; rename : Graph [List-of Symbol] -> Graph
;; Renames the nodes in the graph to the names in the list of symbols
;; Assume that: the list of symbols is the same length as the graph's nodes list;
;; the given list has no duplicates
(check-satisfied (rename graph1 '(newa newb newc newd newe newf))
                 (make-graph=? (make-graph '(newa newb newc newd newe newf)
                                           (λ (sym) (cond [(symbol=? sym 'newa) '(newb newc newd)]
                                                          [(symbol=? sym 'newc) '(newd)]
                                                          [(symbol=? sym 'newf)
                                                           '(newa newb newc newd newe)]
                                                          [else '()])))))
(check-satisfied (rename graph2 '(my cool new symbols))
                 (make-graph=? (make-graph '(my cool new symbols)
                                           (λ (sym) (cond [(symbol=? sym 'my) '(cool)]
                                                          [(symbol=? sym 'cool) '(my new)]
                                                          [(symbol=? sym 'new) '(cool symbols)]
                                                          [(symbol=? sym 'symbols) '(new)])))))
(define (rename g los)
  (local [(define g-nodes (graph-nodes g))
          (define g-neighb (graph-neighbors g))]
    (make-graph los
                (λ (sym) (map (λ (neighbor) (in-other-list neighbor los g-nodes symbol=?))
                              (g-neighb (in-other-list sym g-nodes los symbol=?)))))))

;; in-other-list : {X, Y} Y [List-of X] [List-of Y] [Y Y -> Boolean] -> X
;; Changes the Y from the old list to the corresponding X in the new list
;; Assume that both lists are the same length and y exists in oldlist
(check-expect (in-other-list 'a '(1 2 3 4) '(a b c d) symbol=?) 1)
(check-expect (in-other-list 'c '(1 2 3 4) '(a b c d) symbol=?) 3)
(define (in-other-list y newlist oldlist eq)
  (cond [(empty? newlist) (error "item not found in list")] ;; shouldn't reach this
        [(cons? newlist) (if (eq (first oldlist) y)
                             (first newlist)
                             (in-other-list y (rest newlist) (rest oldlist) eq))]))


;; Exercise 3
; node-name->numbers : Symbol -> (list Nat Nat)
; Convert a symbol of the form 'n1->n2 to (list n1 n2)
(define (node-name->numbers s)
  (map string->number (string-split (symbol->string s) "->")))
(check-expect (node-name->numbers '0->3) '(0 3))


;; swap : Graph -> Graph
;; Swaps a graph's nodes with its edges
(check-satisfied (swap (make-graph '(a b c)
                                   (λ (n) (cond [(symbol=? n 'a) '(b c)]
                                                [(symbol=? n 'b) '(b)]
                                                [(symbol=? n 'c) '(a)]))))
                 (make-graph=? (make-graph '(0->1 0->2 1->1 2->0)
                                           (λ (n) (cond [(symbol=? n '0->1) '(1->1)]
                                                        [(symbol=? n '0->2) '(2->0)]
                                                        [(symbol=? n '1->1) '(1->1)]
                                                        [(symbol=? n '2->0) '(0->1 0->2)])))))
(check-satisfied (swap graph2)
                 (make-graph=? (make-graph (list '0->1 '1->0 '1->2 '2->1 '2->3 '3->2)
                                           (λ (sym) (cond [(symbol=? sym '0->1) '(1->0 1->2)]
                                                          [(symbol=? sym '1->0) '(0->1)]
                                                          [(symbol=? sym '1->2) '(2->1 2->3)]
                                                          [(symbol=? sym '2->1) '(1->0 1->2)]
                                                          [(symbol=? sym '2->3) '(3->2)]
                                                          [(symbol=? sym '3->2) '(2->1 2->3)])))))
(define (swap g)
  (make-graph (swap-nodes g) (swap-neighbors g)))


;; swap-nodes : Graph -> [List-of Symbol]
;; Produces a list of the graph's edges
(check-expect (swap-nodes graph2) (list '0->1 '1->0 '1->2 '2->1 '2->3 '3->2))
(check-expect (swap-nodes graph4) (list '0->1 '0->2 '0->3 '2->3 '5->0 '5->1 '5->2 '5->3 '5->4))
(check-expect (swap-nodes (make-graph '(a b c)
                                      (λ (n) (cond [(symbol=? n 'a) '(b c)]
                                                   [(symbol=? n 'b) '(b)]
                                                   [(symbol=? n 'c) '(a)]))))
              '(0->1 0->2 1->1 2->0))
(define (swap-nodes g)
  (local [(define g-nodes (graph-nodes g))
          (define g-neighb (graph-neighbors g))
          (define node-indices (build-list (length g-nodes) identity))
          ;; node->edges : Symbol Number -> [List-of Symbol]
          ;; Given a node in the graph and its index, produces a list of edges (representations of
          ;; the node pointing to another node)
          (define (node->edges sym num)
            (map (λ (neighbor)
                   (string->symbol (string-append (number->string num)
                                                  "->"
                                                  (number->string (in-other-list neighbor
                                                                                 node-indices
                                                                                 g-nodes
                                                                                 symbol=?)))))
                 (g-neighb sym)))]
    (foldr append '() (map node->edges g-nodes node-indices))))

;; swap-neighbors : Graph -> [Symbol -> [List-of Symbol]]
;; Produces a function that, given an edge of the graph, returns the edges that
;; can be found at the end of the edge
(check-expect ((swap-neighbors (make-graph '(a b c)
                                           (λ (n) (cond [(symbol=? n 'a) '(b c)]
                                                        [(symbol=? n 'b) '(b)]
                                                        [(symbol=? n 'c) '(a)]))))
               '0->1)
              ((λ (n) (cond [(symbol=? n '0->1) '(1->1)]
                            [(symbol=? n '0->2) '(2->0)]
                            [(symbol=? n '1->1) '(1->1)]
                            [(symbol=? n '2->0) '(0->1 0->2)]))
               '0->1))
(check-expect ((swap-neighbors (make-graph '(a b c)
                                           (λ (n) (cond [(symbol=? n 'a) '(b c)]
                                                        [(symbol=? n 'b) '(b)]
                                                        [(symbol=? n 'c) '(a)]))))
               '0->2)
              ((λ (n) (cond [(symbol=? n '0->1) '(1->1)]
                            [(symbol=? n '0->2) '(2->0)]
                            [(symbol=? n '1->1) '(1->1)]
                            [(symbol=? n '2->0) '(0->1 0->2)]))
               '0->2))
(check-expect ((swap-neighbors (make-graph '(a b c)
                                           (λ (n) (cond [(symbol=? n 'a) '(b c)]
                                                        [(symbol=? n 'b) '(b)]
                                                        [(symbol=? n 'c) '(a)]))))
               '1->1)
              ((λ (n) (cond [(symbol=? n '0->1) '(1->1)]
                            [(symbol=? n '0->2) '(2->0)]
                            [(symbol=? n '1->1) '(1->1)]
                            [(symbol=? n '2->0) '(0->1 0->2)]))
               '1->1))
(check-expect ((swap-neighbors (make-graph '(a b c)
                                           (λ (n) (cond [(symbol=? n 'a) '(b c)]
                                                        [(symbol=? n 'b) '(b)]
                                                        [(symbol=? n 'c) '(a)]))))
               '2->0)
              ((λ (n) (cond [(symbol=? n '0->1) '(1->1)]
                            [(symbol=? n '0->2) '(2->0)]
                            [(symbol=? n '1->1) '(1->1)]
                            [(symbol=? n '2->0) '(0->1 0->2)]))
               '2->0))
;; note that the edges of graph2 are (list '0->1 '1->0 '1->2 '2->1 '2->3 '3->2)
(check-expect ((swap-neighbors graph2) '0->1)
              ((λ (sym) (cond [(symbol=? sym '0->1) '(1->0 1->2)]
                              [(symbol=? sym '1->0) '(0->1)]
                              [(symbol=? sym '1->2) '(2->1 2->3)]
                              [(symbol=? sym '2->1) '(1->0 1->2)]
                              [(symbol=? sym '2->3) '(3->2)]
                              [(symbol=? sym '3->2) '(2->1 2->3)])) '0->1))
(check-expect ((swap-neighbors graph2) '1->0)
              ((λ (sym) (cond [(symbol=? sym '0->1) '(1->0 1->2)]
                              [(symbol=? sym '1->0) '(0->1)]
                              [(symbol=? sym '1->2) '(2->1 2->3)]
                              [(symbol=? sym '2->1) '(1->0 1->2)]
                              [(symbol=? sym '2->3) '(3->2)]
                              [(symbol=? sym '3->2) '(2->1 2->3)])) '1->0))
(check-expect ((swap-neighbors graph2) '1->2)
              ((λ (sym) (cond [(symbol=? sym '0->1) '(1->0 1->2)]
                              [(symbol=? sym '1->0) '(0->1)]
                              [(symbol=? sym '1->2) '(2->1 2->3)]
                              [(symbol=? sym '2->1) '(1->0 1->2)]
                              [(symbol=? sym '2->3) '(3->2)]
                              [(symbol=? sym '3->2) '(2->1 2->3)])) '1->2))
(check-expect ((swap-neighbors graph2) '2->1)
              ((λ (sym) (cond [(symbol=? sym '0->1) '(1->0 1->2)]
                              [(symbol=? sym '1->0) '(0->1)]
                              [(symbol=? sym '1->2) '(2->1 2->3)]
                              [(symbol=? sym '2->1) '(1->0 1->2)]
                              [(symbol=? sym '2->3) '(3->2)]
                              [(symbol=? sym '3->2) '(2->1 2->3)])) '2->1))
(check-expect ((swap-neighbors graph2) '2->3)
              ((λ (sym) (cond [(symbol=? sym '0->1) '(1->0 1->2)]
                              [(symbol=? sym '1->0) '(0->1)]
                              [(symbol=? sym '1->2) '(2->1 2->3)]
                              [(symbol=? sym '2->1) '(1->0 1->2)]
                              [(symbol=? sym '2->3) '(3->2)]
                              [(symbol=? sym '3->2) '(2->1 2->3)])) '2->3))
(check-expect ((swap-neighbors graph2) '3->2)
              ((λ (sym) (cond [(symbol=? sym '0->1) '(1->0 1->2)]
                              [(symbol=? sym '1->0) '(0->1)]
                              [(symbol=? sym '1->2) '(2->1 2->3)]
                              [(symbol=? sym '2->1) '(1->0 1->2)]
                              [(symbol=? sym '2->3) '(3->2)]
                              [(symbol=? sym '3->2) '(2->1 2->3)])) '3->2))
(define (swap-neighbors g)
  (local [(define g-nodes (graph-nodes g))
          (define edges (swap-nodes g))
          (define indices (build-list (length g-nodes) identity))
          ;; index : Symbol [List-of Symbol] -> Nat
          ;; Gives the index of the symbol in the list, assuming it exists in the list
          (define (index n list)
            (in-other-list n indices list symbol=?))
          ;; points-to : Symbol -> Symbol
          ;; Gives the node at the end of the edge (the one being pointed to)
          (define (points-to edge)
            (in-other-list (second (node-name->numbers edge)) g-nodes indices =))
          ;; corresponding-edges : Symbol -> [List-of Symbol]
          ;; Return the edges that correspond with the new set of nodes (the old edges)
          (define (corresponding-edges sym)
            (filter (λ (edge) (= (first (node-name->numbers edge)) (index sym g-nodes)))
                    edges))]
    (λ (sym) (corresponding-edges (points-to sym)))))


;Excercise 4

;; close? : Graph Symbol Symbol Natural -> Boolean
;; is there a path from n1 to n2 shorter than or equal to lim?
(define (close? g n1 n2 lim)
  (local [(define result (shortest-path g n1 n2 0 '() lim))]
    (cond [(false? result) #f]
          [(number? result) (not (> result lim))])))
(check-expect (close? graph1 'a 'c 2) #t)
(check-expect (close? graph2 'y 'w 3) #t)
(check-expect (close? graph2 'y 'w 1) #f)
(check-expect (close? graph4 'e 'f 400) #f)

;; in-list? : Symbol [List-of Symbol]
;; is a in list?
(define (in-list? a list)
  (ormap (lambda (sym) (symbol=? a sym)) list))
(check-expect (in-list? 'a (list 'a 'b 'c)) #t)
(check-expect (in-list? 'd (list 'a 'b 'c)) #f)

;; shortest-path : Graph Symbol Symbol Natural [List-of Symbol] -> [Maybe Number]
;; computes the length of the shortest path from n1 to n2 and if there is no path, returns false
;; ACCUMULATOR: oldnodes contains a list of nodes alreday visited
;; ACCUMULATOR: sofar represents the length of the path so far
;; TERMINATES because every call, oldnodes grows and eventually will contain the entire graph,
;; so we won't be able to go to a new node
;; Note: this is an accumulator-based function and isn't meant to be used outside of close?
;; but is defined globally for testing purposes.
(define (shortest-path g n1 n2 sofar oldnodes lim)
  (local [(define new-oldnodes (cons n1 oldnodes))
          (define nodes-to-check
            (filter (lambda (node) (not (in-list? node oldnodes))) ((graph-neighbors g) n1)))]
    ;; nodes-to-check is n1's neighbors that aren't in oldnodes, ie all possible nodes that
    ;; could be on a path to n2
    (cond [(symbol=? n1 n2) sofar]
          [(in-list? n2 nodes-to-check) (add1 sofar)]
          [(not (in-list? n2 nodes-to-check))
           (local [(define paths
                     (map
                      (lambda (node) (shortest-path g node n2 (add1 sofar) new-oldnodes lim))
                      nodes-to-check))

                   ;; get-shortest : [List-of [Maybe Number]] Number -> Number
                   ;; returns the smallest number from a list of [Maybe Number]
                   (define (get-shortest list shortest)
                     (cond [(empty? list) shortest]
                           [(cons? list) (if (and (number? (first list)) (< (first list) shortest))
                                             (get-shortest (rest list) (first list))
                                             (get-shortest (rest list) shortest))]))]
             (if (ormap number? paths) (get-shortest paths (add1 lim)) #f))])))

(check-expect (shortest-path graph1 'a 'c 0 '() 4) 1)
(check-expect (shortest-path graph1 'c 'c 0 '() 4) 0)
(check-expect (shortest-path graph2 'y 'w 0 '() 4) 2)
(check-expect (shortest-path graph2 'y 'x 0 '() 4) 1)
(check-expect (shortest-path graph4 'e 'c 0 '() 4) #f)

;; Excercise 5

(define G1
  (make-graph '(A B C D E F G)
              (λ (n)
                (cond [(symbol=? n 'A) '(B E)]
                      [(symbol=? n 'B) '(E F)]
                      [(symbol=? n 'C) '(D)]
                      [(symbol=? n 'D) '()]
                      [(symbol=? n 'E) '(C F A)]
                      [(symbol=? n 'F) '(D G)]
                      [(symbol=? n 'G) '()]))))


;;find-all-paths : Graph Symbol Symbol -> [List-of [List-of Symbol]]
;; returns a list of paths from n1 to n2
(define (find-all-paths g n1 n2)
  (generate-next-phase g n2 (list (list n1))))

(check-expect (find-all-paths graph1 'a 'c) '((a c)))
(check-expect (find-all-paths graph2 'z 'w) '((z y x w)))
(check-expect (find-all-paths graph2 'y 'x) '((y x)))
(check-expect (find-all-paths G1 'C 'C) '((C)))
(check-expect (find-all-paths G1 'C 'G) '())
(check-expect (find-all-paths G1 'A 'B) '((A B)))
(check-expect (find-all-paths G1 'E 'G) '((E F G) (E A B F G)))
(check-expect (find-all-paths G1 'B 'G) '((B F G) (B E F G)))                                        
(check-expect (find-all-paths G1 'A 'G) '((A B F G) (A E F G) (A B E F G)))

;; next-step : Graph Symbol Symbol [List-of Symbol] -> [List-of Symbol]
;; returns a list of all the neighbors of n1 that lead to n2
(define (next-step g n1 n2 oldnodes)
  (local [(define new-oldnodes (cons n1 oldnodes))
          (define nodes-to-check
            (filter (lambda (node) (not (in-list? node oldnodes))) ((graph-neighbors g) n1)))
          (define good-paths (filter
                              (lambda (node)
                                (number?
                                 (shortest-path g node n2 0 new-oldnodes (add1 (length
                                                                                (graph-nodes g))))))
                              nodes-to-check))]
    good-paths))
(check-expect (next-step graph1 'a 'c '()) (list 'c))
(check-expect (next-step G1 'A 'A '()) '())
(check-expect (next-step G1 'A 'G '()) (list 'B 'E))
(check-expect (next-step G1 'B 'G '()) (list 'E 'F))

;; add-prior : [List-of Symbol] [List-of Symbol] -> [List-of [List-of Symbol]]
;; appends prior to every element in step, designed for use in conjunction with next-step 
(define (add-prior prior step)
  (map (lambda (symbol) (append prior (list symbol))) step))
(check-expect (add-prior '(a b) '(c d)) '((a b c) (a b d)))
(check-expect (add-prior '(a b) '()) '())

;; one-step : Graph Symbol Symbol [List-of Symbol] -> [List-of [List-of Symbol]]
;; computes a single step from n1 toward n2 and returns the next step of the paths in list format
(define (one-step g n1 n2 prior)
  (local [(define newprior (append prior (list n1)))
          (define list-of-lists (add-prior newprior (next-step g n1 n2 newprior)))]
    list-of-lists))
(check-expect (one-step G1 'A 'G '()) (list (list 'A 'B) (list 'A 'E)))
(check-expect (one-step G1 'B 'G '()) (list (list 'B 'E) (list 'B 'F)))
(check-expect (one-step G1 'G 'A '(B C D E)) '())

;; condense : [List-of [List-of [List-of Symbol]]] -> [List-of [List-of Symbol]]
;; condenses listy into a [List-of [List-of Symbol]] by pulling out components from the inner lists
(define (condense listy)
  (foldr (lambda (list rest) (append (map (lambda (innerlist) innerlist) list) rest)) '() listy))

(check-expect (condense (list
                         (list (list 'A 'B 'E) (list 'A 'B 'F))
                         (list (list 'A 'E 'F))))
              (list (list 'A 'B 'E) (list 'A 'B 'F) (list 'A 'E 'F)))
(check-expect (condense (list (list '(A B C) '(D E F)))) (list (list 'A 'B 'C) (list 'D 'E 'F)))

;; generate-next-phase : Graph Symbol [List-of [List-of Symbol]] -> [List-of [List-of Symbol]]
;; generates the paths from the endpoint of each path in listofpaths to n2
;; ACCUMULATOR : listofpaths is the list paths that go from the initial start to this step.
;; TERMINATES : listofpaths grows with each recursive call and will eventually contain every path from
;; the initial node to the target node, so next-paths will be empty, stopping the recursion.
(define (generate-next-phase g n2 listofpaths)
  (local [(define listofnodes (paths->last-nodes listofpaths))
          ;; remove-lasts : [List-of [List-of Symbol]] -> [List-of [List-of Symbol]]
          ;; removes the last symbol from each list in lop
          (define (remove-lasts lop)
            (map (lambda (path) (all-but-last path)) lop))

          ;; all-but-last : [List-of Symbol] -> [List-of Symbol]
          ;; removes the last symbol from list
          (define (all-but-last list)
            (cond [(empty? list) '()]
                  [(empty? (rest list)) '()]
                  [(cons? (rest list)) (cons (first list) (all-but-last (rest list)))]))
          
          (define new-paths (remove-lasts listofpaths))
          (define complete-paths (filter (lambda (path) (symbol=? (last path) n2)) listofpaths))
          ;; Paths that are already done; record them to append them back onto next-paths later
          (define next-paths (condense (map (lambda (path node) (one-step g node n2 path))
                                            new-paths listofnodes)))
          (define all-paths (append complete-paths next-paths))]
    (cond [(empty? next-paths) all-paths]
          [(list? next-paths) (generate-next-phase g n2 all-paths)])))

(check-expect (generate-next-phase G1 'G (list (list 'A))) (list
                                                            (list 'A 'B 'F 'G)
                                                            (list 'A 'E 'F 'G)
                                                            (list 'A 'B 'E 'F 'G)))

(check-expect (generate-next-phase G1 'G
                                   (list
                                    (list 'A 'B 'E 'F)
                                    (list 'A 'B 'F 'G)
                                    (list 'A 'E 'F 'G)))
              (list
               (list 'A 'B 'F 'G)
               (list 'A 'E 'F 'G)
               (list 'A 'B 'E 'F 'G)))

;; paths->last-nodes : [List-of [List-of Symbol]] -> [List-of Symbol]
;; returns a list containing the last symbol from each list in lop
(define (paths->last-nodes lop)
  (map (lambda (list) (last list)) lop))
(check-expect (paths->last-nodes (list (list 'A 'B) (list 'A 'E))) '(B E))
(check-expect (paths->last-nodes (list (list 'A 'B 'C) (list 'A 'E) (list 'A 'E))) '(C E E))

;; last : {X} [List-of X] -> X
;; returns the last item in the list
(define (last list)
  (cond [(empty? (rest list)) (first list)]
        [else (last (rest list))]))
(check-expect (last '(2 3 4 6 7 7)) 7)
(check-expect (last '("ow" "hungry" "bit" "byte" "no" "hi")) "hi")

;;Excercise 6

;; connected? : Graph -> Boolean
;; are all the graph's nodes connected to one another?
(define (connected? g)
  (local [(define gnodes (graph-nodes g))]
    (andmap (lambda (node) (andmap (lambda (node2) (number? (shortest-path g node node2 0 '()
                                                                           (length gnodes))))
                                   gnodes))
            gnodes)))

(check-expect (connected? graph1) #f)
(check-expect (connected? graph2) #t)

(check-satisfied graph1 (lambda (x) (not (connected? x))))
(check-satisfied graph2 connected?)

;; Excercise 7

;; undirected? : Graph -> Boolean
;; does each edge in g have a corresponging edge in the other direction?
(define (undirected? g)
  (andmap (lambda (node) (andmap (lambda (node1)
                                   (if (neighbor-of? g node1 node) (neighbor-of? g node node1) #t))
                                 (graph-nodes g))) (graph-nodes g)))

(check-expect (undirected? graph1) #f)
(check-expect (undirected? graph2) #t)

(check-satisfied graph1 (lambda (x) (not (undirected? x))))
(check-satisfied graph2 undirected?)


;; Excercise 8 EXTRA CREDIT LETS GOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO


(define (graph-shape=? g1 g2)
  (and (= (length (graph-nodes g1)) (length (graph-nodes g2)))
       (graph=? (rename g2 (graph-nodes g1)) g1)))

(check-expect (graph-shape=? graph1 graph1) #t)
(check-expect (graph-shape=? graph1 graph2) #f)
(check-expect (graph-shape=? graph3 (make-graph '(a b c fakenews)
                                                (λ (sym) (cond [(symbol=? sym 'a) '(b)]
                                                               [(symbol=? sym 'b) '(c)]
                                                               [(symbol=? sym 'c) '(fakenews)]
                                                               [(symbol=? sym 'fakenews) '()])))) #t)
(check-expect (graph-shape=? graph1 graph4) #f)
