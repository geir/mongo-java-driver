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

(def mongo (org.mongodb.driver.ts.Mongo.))
(def db (.getDB mongo "clojure"))
(def coll (.getCollection db "test"))

(. coll clear)                          ; erase all records in the collection

; insert three records
(dorun (map #(do (.insert coll {"a" (+ % 1)})) (range 0 3)))

; print the number of records in the collection.
(println "There are" (.getCount coll (org.mongodb.driver.ts.MongoSelector.)) "records in the collection 'test'")

; one way to do a query
(loop [i (.find coll)]
  (when i
    (do (println (first i))
        (recur (rest i)))))

; and another
(dorun (map println (.find coll)))
