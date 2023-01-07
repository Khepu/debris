(ns debris.core
  (:require
   [gloss.io :as io]
   [aleph.tcp :as tcp]
   [manifold.deferred :as d]
   [manifold.stream :as stream]
   [debris.network.protocol :refer [protocol]]
   [clojure.tools.logging :as log])
  (:gen-class))

(def clients (atom #{}))

(defn wrap-duplex-stream
  [protocol s]
  (let [out (stream/stream)]
    (stream/connect (stream/map #(io/encode protocol %) out) s)
    (stream/splice out (io/decode-stream s protocol))))

(defn append-client
  [client-stream]
  (swap! clients conj client-stream))

(defn fast-echo-handler
  [f]
  (fn [input-stream info]
    (append-client input-stream)
    (stream/connect
     (stream/map f input-stream)
     input-stream)))

(defn start-server
  [handler config]
  (tcp/start-server
   (fn [input-stream info]
     (handler (wrap-duplex-stream protocol input-stream) info))
   config))

(defn -main
  [& args]
  (let [config {:port 10000}
        server (start-server (fast-echo-handler identity) config)]
    (log/info "Started TCP Server at port" (:port config))
    server))
