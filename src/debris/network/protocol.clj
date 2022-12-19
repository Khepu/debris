(ns debris.network.protocol
  (:require
   [clojure.edn :as edn]
   [gloss.core :as gloss]))

(def protocol
  (gloss/compile-frame
   (gloss/finite-frame :uint32
                       (gloss/string :utf-8))
   pr-str
   edn/read-string))
