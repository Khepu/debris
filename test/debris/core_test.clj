(ns debris.core-test
  (:require
   [clojure.test :refer :all]
   [debris.core :refer :all]
   [aleph.tcp :as tcp]
   [manifold.deferred :as d]
   [manifold.stream :as stream]
   [debris.network.protocol :refer [protocol]]))

(defn client
  [host port]
  (d/chain (tcp/client {:host host :port port})
           #(wrap-duplex-stream protocol %)))

(deftest a-test
  (testing "Basic connecivity"
    (is (= 1
           (let [server (-main)
                 c @(client "localhost" 10000)]
             (try
               @(stream/put! c 1)
               @(stream/take! c)
               (catch Exception e nil)
               (finally
                 (.close server))))))))
