;
;      Copyright (C) 2008 Geir Magnusson Jr
;
;    Licensed under the Apache License, Version 2.0 (the "License");
;    you may not use this file except in compliance with the License.
;    You may obtain a copy of the License at
;
;       http://www.apache.org/licenses/LICENSE-2.0
;
;    Unless required by applicable law or agreed to in writing, software
;    distributed under the License is distributed on an "AS IS" BASIS,
;    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;    See the License for the specific language governing permissions and
;    limitations under the License.
;

;
; To run:
; $ cd src/examples/clojure
; $ clj # or however you normally start the clojure REPL
; Clojure
; user=> (add-classpath "file:///full/path/to/mongo-driver.jar")
; nil
; user=> (load-file "mongo.clj")
;

(def mongo (org.mongodb.driver.dyn.Mongo.))
(def db (.getDB mongo "clojure"))
(def coll (.getCollection db "test"))

; Erase all records in the collection.
(. coll clear)

; Insert five records.
(dotimes [i 5] (.insert coll {"a" (inc i)}))

; Print the number of records in the collection.
(println "There are" (.getCount coll) "records in the collection 'test'")

; One way to do a query. Note that we turn the returned results into a seq. If
; you don't do that, you won't see all the records because of the way Clojure
; treats iterable objects in Java.
(loop [i (seq (.find coll))]
  (when i
    (do (println (first i))
        (recur (rest i)))))

; Another way to perform a query. Again, we turn the results into a seq.
(dorun (map println (seq (.find coll))))

; Yet another way. We don't have to turn the results into a seq manually;
; doseq seems to do that already.
(doseq [i (.find coll)] (println i))

; And yet another. This time you don't have to turn the results into a seq
; manually because we are calling the cursor object's hasNext and next methods
; directly.
(let [cursor (.find coll)]
  (while (.hasNext cursor)
         (println (.next cursor))))
