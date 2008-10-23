(def mongo (new org.mongo.driver.impl.Mongo))
(def db (. mongo (getDB "clojure"))

(def coll (. db (getCollection "test" true)))

(. coll clear)

; we have to use the mongoDoc as clojure doesn't do Map

(def md (new org.mongo.driver.MongoDoc))

(. md (put "a" 1))

(. coll (insert md))

(def res (. coll find))

;
; one way to do a query
;

(loop [i res]
        (println (first i))
        (when (rest i) (recur (rest i)))
)

;
; and another
;

(map println (. coll find))

